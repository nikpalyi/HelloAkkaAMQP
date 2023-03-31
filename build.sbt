ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "HelloAkkaAMQP"
  )

val AkkaVersion = "2.6.14"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "3.0.4",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
)