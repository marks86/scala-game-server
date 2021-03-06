package com.gmail.namavirs86.app.controllers


import scala.util.Random
import akka.util.Timeout
import scala.concurrent._
import ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import com.gmail.namavirs86.game.card.core.Definitions.{Flow, GameContext, RequestContext}
import com.gmail.namavirs86.game.card.core.Game
import com.gmail.namavirs86.game.card.core.protocols.CoreJsonProtocol
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.app.repositories.GameRepo
import com.gmail.namavirs86.app.Configuration

// @TODO: add available actions to response
trait GameController extends CoreJsonProtocol with GameRepo {

  def games: Games

  implicit def system: ActorSystem

  implicit lazy val timeout: Timeout = Timeout(Configuration.gameResponseTimeout)
  lazy val log = Logging(system, classOf[GameController])

  lazy val gameRoutes: Route =
    pathEnd {
      get {
        entity(as[RequestContext]) { request =>
          gameRequestHandler(request)
        }
      }
    }

  private def createFlow(requestContext: RequestContext): Future[Flow[GameContext]] = {
    val gameId = requestContext.gameId
    val gameContextFuture = fetchGameContext(gameId, userId = 0)

    gameContextFuture.map { gameContext =>
      Flow(
        requestContext = requestContext,
        gameContext = gameContext,
        response = None,
        rng = new Random(),
      )
    }
  }

  private def gameRequestHandler(request: RequestContext): Route = {
    val gameId = request.gameId

    games.get(gameId) match {
      case Some(gameRef) ⇒
        val flowFuture = createFlow(request)

        // @TODO: using scala-async will be more readable, check
        onSuccess(flowFuture) { flow =>
          val responsePlay: Future[Game.ResponsePlay[GameContext]] =
            (gameRef ? Game.RequestPlay(flow)).mapTo[Game.ResponsePlay[GameContext]]

          onSuccess(responsePlay) { responsePlay =>
            val flow = responsePlay.flow
            val gameId = flow.requestContext.gameId
            val gameContext = flow.gameContext
            updateGameContext(gameId, userId = 0, gameContext)

            flow.response match {
              case Some(response) ⇒ complete(response)
              case None ⇒ complete(StatusCodes.OK)
            }
          }
        }
      case None => complete("No game found")
    }
  }
}

//curl -H "Content-Type: application/json" -X GET -d '{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 0.01}' http://localhost:8080/game