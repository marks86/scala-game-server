package com.gmail.namavirs86.app

import akka.actor.ActorRef
import com.gmail.namavirs86.game.card.core.Definitions.GameId

object Definitions {
  type Games = Map[GameId, ActorRef]
}
