
val scalaTestVersion = "3.0.5"
val shapelessVersion = "2.3.3"

lazy val ShapelessGuide = (project in file(".")).
    settings(
        organization := "com.hibiup.shappless.examples",
        name := "ShapelessGuide",
        version := "0.1",
        scalaVersion := "2.12.8",
        libraryDependencies ++= Seq(
            "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
            "com.chuusai" %% "shapeless" % "2.3.3"
        ),
        addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full),
        scalacOptions ++= Seq(
            "-Xplugin-require:macroparadise",
            "-language:higherKinds",
            "-deprecation",
            "-encoding", "UTF-8",
            "-Ypartial-unification",
            "-feature",
            "-language:_"
        )
    )