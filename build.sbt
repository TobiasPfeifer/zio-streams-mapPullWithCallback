scalaVersion := "2.13.3"
scalacOptions += "-language:higherKinds"

scalacOptions += "-Ydelambdafy:inline"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "1.0.3",
  "dev.zio" %% "zio-streams" % "1.0.3",
)
turbo := true
useSuperShell := false
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)