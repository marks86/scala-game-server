package com.gmail.namavirs86.app

import akka.actor.ActorSystem
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.game.blackjack

object Loader {

  def load(system: ActorSystem): Games = {
    Map(
      blackjack.id → system.actorOf(blackjack.props, blackjack.id)
    )
  }

}
