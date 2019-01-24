package com.gmail.namavirs86.app

import akka.actor.ActorSystem
import com.gmail.namavirs86.app.Definitions.Games
import com.gmail.namavirs86.game.blackjack
import com.gmail.namavirs86.game.card.core.Game

object Loader {

  def load(system: ActorSystem): Games = {
    Map(
      blackjack.config.id → system.actorOf(Game.props(blackjack.config))
    )
  }

}
