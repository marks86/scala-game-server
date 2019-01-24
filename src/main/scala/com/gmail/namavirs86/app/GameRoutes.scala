package com.gmail.namavirs86.app

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, entity, onSuccess, pathEnd, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.gmail.namavirs86.app.Definitions.GamePlayRequest

import scala.concurrent.Future
import scala.concurrent.duration._

trait GameRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[GameRoutes])

//  def games: Map[String, ActorRef]

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val gameRoutes: Route =
    pathPrefix("game") {
      pathEnd {
        get {
          entity(as[GamePlayRequest]) { gamePlayRequest =>
            complete((StatusCodes.Accepted, gamePlayRequest))
            //            pprint.pprintln(gamePlayRequest)
            //            val gameId = gamePlayRequest.gameId
            //
            //            games.get(gameId) match {
            //              case Some(gameRef) ⇒
            //                (gameRef ? )
            //            }

            //            val gamePlayResponse: Future[ActionPerformed] =
            //              (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
            //
            //
            //            onSuccess(userCreated) { performed =>
            //              log.info("Created user [{}]: {}", user.name, performed.description)
            //              complete((StatusCodes.Created, performed))
            //            }


          }
        }
      }
    }
}
