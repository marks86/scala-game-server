package com.gmail.namavirs86.app.controllers

import scala.util.Random
import akka.util.Timeout
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask

import com.gmail.namavirs86.game.card.core.Definitions.{Flow, GameContext, GameId, RequestContext}
import com.gmail.namavirs86.game.card.core.Game
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.app.protocols.GameJsonProtocol
import com.gmail.namavirs86.app.repositories.GameRepo


// @TODO: add available actions to response
// @TODO: add user balance
// @TODO: each request action validation
// @TODO: add init request (probably)
// @TODO: exception handling

trait GameController extends GameJsonProtocol with GameRepo {

  implicit def system: ActorSystem

  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  lazy val log = Logging(system, classOf[GameController])

  def games: Games

  lazy val gameRoutes: Route =
    pathEnd {
      get {
        entity(as[RequestContext]) { request =>
          gameRequestHandler(request)
        }
      }
    }

  private def createFlow(requestContext: RequestContext): Future[Flow] = {
    val gameId = requestContext.gameId
    val gameContextFuture = fetchGameContext(gameId, 0)

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

        onSuccess(flowFuture) { flow =>
          val responsePlay: Future[Game.ResponsePlay] =
            (gameRef ? Game.RequestPlay(flow)).mapTo[Game.ResponsePlay]

          onSuccess(responsePlay) { responsePlay =>
            val gameId = responsePlay.flow.requestContext.gameId
            val gameContext = responsePlay.flow.gameContext
            updateGameContext(gameId, 0, gameContext)
            complete(responsePlay.flow.response)
          }
        }
      case None => complete("No game found")
    }
  }
}

//curl -H "Content-Type: application/json" -X GET -d '{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 0.01}' http://localhost:8080/game