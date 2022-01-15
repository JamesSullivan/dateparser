organizationName := "asia.solutions"

name := "dateParser"

version := "0.0.2"

scalaVersion := "2.13.8"

startYear := Some(2017)

licenses += ("MIT", new URL("https://opensource.org/licenses/MIT"))

// https://mvnrepository.com/artifact/org.scalameta/semanticdb-scalac
semanticdbVersion := "4.4.32" // added for Scala 2.13.5 error may be able to remove in future

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "com.lihaoyi" %% "fastparse" % "2.3.3"
)
       
scalacOptions ++= Seq(
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-unchecked",
)
