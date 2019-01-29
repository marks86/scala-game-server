package com.gmail.namavirs86.app.protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.gmail.namavirs86.game.card.core.Definitions.Outcome.Outcome
import com.gmail.namavirs86.game.card.core.Definitions.Rank.Rank
import com.gmail.namavirs86.game.card.core.Definitions.RequestType.RequestType
import com.gmail.namavirs86.game.card.core.Definitions.Suit.Suit
import com.gmail.namavirs86.game.card.core.Definitions._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

trait GameJsonProtocol extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit object requestTypeFormat extends JsonFormat[RequestType] {
    val string2RequestType: Map[JsValue, RequestType] =
      RequestType.requestTypes.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): RequestType = {
      string2RequestType.getOrElse(json, throw DeserializationException("Request type expected"))
    }

    override def write(obj: RequestType): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val gamePlayRequestFormat: RootJsonFormat[RequestContext] = jsonFormat5(RequestContext)

  implicit object suitFormat extends JsonFormat[Suit] {
    val string2Suit: Map[JsValue, Suit] =
      Suit.suits.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Suit = {
      string2Suit.getOrElse(json, throw DeserializationException("Suit type expected"))
    }

    override def write(obj: Suit): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit object rankFormat extends JsonFormat[Rank] {
    val string2Rank: Map[JsValue, Rank] =
      Rank.ranks.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Rank = {
      string2Rank.getOrElse(json, throw DeserializationException("Rank type expected"))
    }

    override def write(obj: Rank): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val cardFormat: RootJsonFormat[Card] = jsonFormat2(Card)

  implicit val responseDealerContextFormat: RootJsonFormat[ResponseDealerContext] = jsonFormat3(ResponseDealerContext)

  implicit object outcomeFormat extends JsonFormat[Outcome] {
    val string2OutcomeType: Map[JsValue, Outcome] =
      Outcome.outcomes.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): Outcome = {
      string2OutcomeType.getOrElse(json, throw DeserializationException("Outcome type expected"))
    }

    override def write(obj: Outcome): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val responsePlayerContextFormat: RootJsonFormat[ResponsePlayerContext] = jsonFormat3(ResponsePlayerContext)

  implicit val gamePlayResponseFormat: RootJsonFormat[GamePlayResponse] = jsonFormat6(GamePlayResponse)

}
