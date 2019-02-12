package com.gmail.namavirs86.app

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

object Configuration {

  private val config: Config = ConfigFactory.load()

  val defaultInterface: String = config.getString("app.defaultInterface")

  val defaultPort: Int = config.getInt("app.defaultPort")

  val gameResponseTimeout: FiniteDuration = FiniteDuration(
    config.getDuration("app.gameResponseTimeout").toMillis,
    MILLISECONDS
  )

}
