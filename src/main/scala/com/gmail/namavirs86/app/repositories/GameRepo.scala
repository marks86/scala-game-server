package com.gmail.namavirs86.app.repositories

import scala.concurrent.Future
import scala.collection.mutable
import scala.concurrent._
import ExecutionContext.Implicits.global
import akka.Done
import com.gmail.namavirs86.game.card.core.Definitions.{GameContext, GameId}

trait GameRepo {

  val contextMap = mutable.Map.empty[String, Option[GameContext]]

  //  (fake) async database query api
  def fetchGameContext(gameId: GameId, userId: Long): Future[Option[GameContext]] = Future {
    val id = userId.toString.concat(gameId)
    contextMap.getOrElse(id, None)
  }

  //  (fake) async database query api
  def updateGameContext(gameId: GameId, userId: Long, gameContext: Option[GameContext]): Future[Done] = Future {
    val id = userId.toString.concat(gameId)
    contextMap(id) = gameContext
    Done
  }

}
