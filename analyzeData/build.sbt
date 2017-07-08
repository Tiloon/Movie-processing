name := "spark-movie"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.1.1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies +=  "com.typesafe.play" %% "play-json" % "2.3.0"

scalaSource in Test := { (baseDirectory in Test)(_ / "tests") }.value
scalaSource in Compile := { (baseDirectory in Compile)(_ / "src") }.value