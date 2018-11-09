name := "cats-training-ground"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.4.0",
  "org.scalaz" %% "scalaz-zio" % "0.3.1"
)
