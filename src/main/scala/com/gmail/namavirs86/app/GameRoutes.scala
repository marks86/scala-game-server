package com.gmail.namavirs86.app

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Game

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

trait GameRoutes extends JsonSupport {

  implicit def system: ActorSystem

  implicit lazy val timeout = Timeout(5.seconds)
  lazy val log = Logging(system, classOf[GameRoutes])

  def games: Games

  var contextMap = Map.empty[Long, GameContext]

  //  (fake) async database query api
  def fetchGameContext(gameId: GameId, userId: Long): Future[GameContext] = Future {
    contextMap.getOrElse(userId, GameContext(
      dealer = DealerContext(
        hand = ListBuffer[Card](),
        value = 0,
        holeCard = None,
        hasBJ = false,
      ),
      player = PlayerContext(
        hand = ListBuffer[Card](),
        value = 0,
        hasBJ = false,
      ),
      shoe = List(),
      bet = None,
      totalWin = 0f,
      outcome = None,
      roundEnded = true,
    ))
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
                    log.info(responsePlay.flow.gameContext.toString)
                    complete(StatusCodes.OK)
                  }
                }
              case None => complete("No game")
            }
          }
        }
      }
    }
}
