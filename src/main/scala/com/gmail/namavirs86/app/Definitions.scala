package com.gmail.namavirs86.app

import com.gmail.namavirs86.app.Definitions.RequestType.RequestType

object Definitions {

  object RequestType {

    sealed abstract class RequestType

    case object INIT extends RequestType

    case object PLAY extends RequestType

    val requestTypes = List(INIT, PLAY)

  }

  final case class GamePlayRequest(
                                    request: RequestType,
                                    gameId: String,
                                    requestId: Long,
                                    action: Option[String], // deal, hit, stand
                                    bet: Option[Float],
                                  )

}

//'{"request": "PLAY", "gameId": "bj", "requestId": 0, "action": "DEAL", "bet": 0.01}'