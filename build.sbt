organizationName := "asia.solutions"

name := "dateParser"

version := "0.0.2"

scalaVersion := "2.13.5"

startYear := Some(2017)

licenses += ("MIT", new URL("https://opensource.org/licenses/MIT"))

semanticdbVersion := "4.4.11" // added for Scala 2.13.5 error may be able to remove in future

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "com.lihaoyi" %% "fastparse" % "2.3.2"
)
       
scalacOptions ++= Seq(
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-unchecked",
)
