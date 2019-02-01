lazy val akkaHttpVersion = "10.1.7"
lazy val akkaVersion = "2.5.19"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      version := "1.0.0",
      organization := "com.gmail.namavirs86",
      scalaVersion := "2.12.8"
    )),
    name := "scala-game-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.lihaoyi" %% "pprint" % "0.5.3"
    )
  )
  .dependsOn(RootProject(uri("https://github.com/marks86/scala-game-card-blackjack.git")))
