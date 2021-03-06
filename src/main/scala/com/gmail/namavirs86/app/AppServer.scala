package com.gmail.namavirs86.app

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.gmail.namavirs86.app.controllers.GameController

object AppServer extends App with GameController {

  implicit val system: ActorSystem = ActorSystem("appServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val games = Loader.load(system)

  lazy val routes: Route = pathPrefix(pm = "game") {
    gameRoutes
  }

  val serverBinding: Future[Http.ServerBinding] = Http()
    .bindAndHandle(
      routes,
      Configuration.defaultInterface,
      Configuration.defaultPort
    )

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  StdIn.readLine()

  serverBinding
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

  //  Await.result(system.whenTerminated, Duration.Inf)
}
