name := "cats-training-ground"

version := "0.1"

scalaVersion := "2.12.7"

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0")

scalacOptions += "-Ypartial-unification"
libraryDependencies ++= Seq(
  "org.typelevel"              %% "cats-core"                 % "1.4.0",
  "org.scalaz"                 %% "scalaz-zio"                % "0.3.1",
  "org.typelevel"              %% "cats-effect"               % "1.0.0",
  "org.typelevel"              %% "cats-laws"                 % "1.1.0" % Test, //or `cats-testkit` if you are using ScalaTest
  "org.typelevel"              %% "cats-testkit"              % "1.1.0" % Test, //or `cats-testkit` if you are using ScalaTest
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % Test
)
