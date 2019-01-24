package com.gmail.namavirs86.app

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.gmail.namavirs86.game.card.core.Definitions.{RequestContext, RequestType}
import com.gmail.namavirs86.game.card.core.Definitions.RequestType.RequestType
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit object requestTypeJsonFormat extends JsonFormat[RequestType] {
    val string2RequestType: Map[JsValue, RequestType] =
      RequestType.requestTypes.map(r => (JsString(r.toString), r)).toMap

    override def read(json: JsValue): RequestType = {
      string2RequestType.getOrElse(json, throw DeserializationException("Request type expected"))
    }

    override def write(obj: RequestType): JsValue = {
      JsString(obj.toString)
    }
  }

  implicit val gamePlayRequestJsonFormat: RootJsonFormat[RequestContext] = jsonFormat5(RequestContext)
}
