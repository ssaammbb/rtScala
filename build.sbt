name := "rtScala"


version := "0.1"

scalaVersion := "2.12.8"


libraryDependencies ++= Seq(
 // "org.scala-lang.modules" %% "scala-swing" % "2.0.3",
//"com.typesafe.akka" %% "akka-http" % "10.0.9"
)

connectInput in run := true


//enablePlugins(JavaAppPackaging)
//set mainClass in (Compile, run) := Some("rt.main")