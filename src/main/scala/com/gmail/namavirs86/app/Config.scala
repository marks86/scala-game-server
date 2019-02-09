package com.gmail.namavirs86.app

import scala.concurrent.duration._

object Config {

  val defaultPort: Int = 8080

  val defaultInterface: String = "localhost"

  val gameResponseTimeout: FiniteDuration = 5.seconds

}
