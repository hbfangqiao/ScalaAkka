import sbt.Keys.{libraryDependencies,unmanagedJars}

lazy val commonSettings = Seq(
  scalaVersion := "2.12.2",
  version := "1.0",
  scalacOptions := Seq("-unchecked","deprecation","-encoding","utf8"),
  javacOptions := Seq("-encoding", "UTF-8")
)


lazy val akka = {
//  val akkaV = "2.4.17"
  val akkaV = "2.5.6"
  val scalaTestV  = "3.0.1"
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-remote" % akkaV,
    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "com.typesafe.akka" %% "akka-http" % "10.0.10",
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "io.spray" %%  "spray-json" % "1.3.3",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.iq80.leveldb"  % "leveldb"  % "0.9",
    "org.fusesource.leveldbjni"  % "leveldbjni-all" % "1.8"
  )
}


lazy val root = Project(id = "scalaakka",base = file("."))
  .settings(commonSettings,
    name := "ScalaAkka",
    libraryDependencies ++= akka
  )



