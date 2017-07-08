name := "persister-movie"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies +=  "com.typesafe.play" %% "play-json" % "2.3.0"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
