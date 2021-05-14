name := "rtScala"

version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "2.0.3"
)

//set mainClass in (Compile, run) := Some("rt.main")