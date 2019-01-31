package com.gmail.namavirs86.app.repositories

import scala.concurrent.Future
import scala.collection.mutable
import scala.concurrent._
import ExecutionContext.Implicits.global
import akka.Done
import com.gmail.namavirs86.game.card.core.Definitions.{GameContext, GameId}

trait GameRepo {

  val contextMap = mutable.Map.empty[Long, Option[GameContext]]

  //  (fake) async database query api
  def fetchGameContext(gameId: GameId, userId: Long): Future[Option[GameContext]] = Future {
    contextMap.getOrElse(userId, None)
  }

  //  (fake) async database query api
  def updateGameContext(gameId: GameId, userId: Long, gameContext: Option[GameContext]): Future[Done] = Future {
    contextMap(userId) = gameContext
    Done
  }

}
