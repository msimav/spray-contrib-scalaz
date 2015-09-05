name := "spray-contrib-scalaz"

version := "0.0.1"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"        % "2.3.12" % "provided",
  "org.scalaz"        %% "scalaz-core"       % "7.1.3"  % "provided",
  "org.scalaz"        %% "scalaz-concurrent" % "7.1.3"  % "provided",
  "io.spray"          %% "spray-client"      % "1.3.3"  % "provided",
  "io.spray"          %% "spray-json"        % "1.3.2"  % "provided",
  "org.scalatest"     %% "scalatest"         % "2.2.4"  % "test",
  "org.scalacheck"    %% "scalacheck"        % "1.12.4" % "test"
)

