import sbt.Keys.libraryDependencies

version := "0.1"

scalaVersion := "2.13.6"

lazy val gamesServer: Project = (project
  in file("."))
  .settings(name := "GamesServer")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.8",
  "com.typesafe.akka" %% "akka-actor" % "2.6.14",
  "org.scalacheck" %% "scalacheck" % "1.14.0",
  "org.typelevel" %% "discipline-scalatest" % "2.1.5" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.6.14" % Test
)
