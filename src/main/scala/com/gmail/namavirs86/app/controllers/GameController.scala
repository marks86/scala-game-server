package com.gmail.namavirs86.app.controllers

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.game.card.core.Definitions.{Flow, GameContext, GameId, RequestContext}
import com.gmail.namavirs86.game.card.core.Game

import scala.concurrent.duration._
import akka.pattern.ask
import com.gmail.namavirs86.app.protocols.GameJsonProtocol

import scala.concurrent.Future
import scala.util.Random

// @TODO: add game context saving to contextMap
// @TODO: add available actions to response
// @TODO: add user balance
// @TODO: each request action validation
// @TODO: add init request (probably)

trait GameController extends GameJsonProtocol {

  implicit def system: ActorSystem

  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  lazy val log = Logging(system, classOf[GameController])

  def games: Games

  var contextMap = Map.empty[Long, GameContext]

  //  (fake) async database query api
  def fetchGameContext(gameId: GameId, userId: Long): Future[Option[GameContext]] = Future {
    contextMap.get(userId)
  }(system.dispatcher)

  //  (fake) async database query api
  def saveGameContext(gameId: GameId, userId: Long, gameContext: GameContext): Future[Done] = Future {
    contextMap += userId → gameContext
    Done
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
                  val gameId = responsePlay.flow.requestContext.gameId
                  val gameContext = responsePlay.flow.gameContext
                  saveGameContext(gameId, 0, gameContext.get)
                  complete(responsePlay.flow.response)
                }
              }
            case None => complete("No game found")
          }
        }
      }
    }

}

//curl -H "Content-Type: application/json" -X GET -d '{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 0.01}' http://localhost:8080/game