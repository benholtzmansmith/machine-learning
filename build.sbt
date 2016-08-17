name := "machine-learning"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq( 
	"org.apache.spark" % "spark-core_2.11" % "2.0.0",
	"com.typesafe.play" % "play-json_2.11" % "2.5.4" exclude("com.fasterxml.jackson.core", "jackson-databind"),
	"org.apache.spark" % "spark-sql_2.11" % "2.0.0",
	"org.mongodb" % "mongo-hadoop-core" % "1.3.0",
	"org.apache.spark" % "spark-mllib_2.11" % "2.0.0",
	"org.cvogt" % "play-json-extensions_2.11" % "0.8.0",
	"org.cvogt" % "scala-extensions_2.11" % "0.5.3"
)