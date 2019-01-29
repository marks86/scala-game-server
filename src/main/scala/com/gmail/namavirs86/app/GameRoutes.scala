package com.gmail.namavirs86.app

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import akka.event.Logging
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Game
import spray.json.JsString


// @TODO: add user balance
// @TODO: each request action validation
// @TODO: add init request (probably)

trait GameRoutes extends JsonSupport {

  implicit def system: ActorSystem

  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  lazy val log = Logging(system, classOf[GameRoutes])

  def games: Games

  var contextMap = Map.empty[Long, GameContext]

  //  (fake) async database query api
  def fetchGameContext(gameId: GameId, userId: Long): Future[Option[GameContext]] = Future {
    contextMap.get(userId)
  }(system.dispatcher)

  def createFlow(requestContext: RequestContext): Future[Flow] = {
    val gameId = requestContext.gameId
    val gameContextFuture = fetchGameContext(gameId, 0)

    gameContextFuture.map { gameContext =>
      Flow(
        requestContext = requestContext,
        gameContext = gameContext,
        response = None,
        rng = new Random(),
      )
    }(system.dispatcher)
  }

  lazy val gameRoutes: Route =
    pathPrefix("game") {
      pathEnd {
        get {
          entity(as[RequestContext]) { requestContext =>
            val gameId = requestContext.gameId

            games.get(gameId) match {
              case Some(gameRef) ⇒
                val flowFuture = createFlow(requestContext)

                onSuccess(flowFuture) { flow =>
                  val responsePlay: Future[Game.ResponsePlay] =
                    (gameRef ? Game.RequestPlay(flow)).mapTo[Game.ResponsePlay]

                  onSuccess(responsePlay) { responsePlay =>
//                    log.info(responsePlay.flow.response.toString)

//                    complete(StatusCodes.OK)
                    complete(responsePlay.flow.response)
                  }
                }
              case None => complete("No game found")
            }
          }
        }
      }
    }
}

//curl -H "Content-Type: application/json" -X GET -d '{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 0.01}' http://localhost:8080/game