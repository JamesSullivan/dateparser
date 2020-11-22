// *****************************************************************************
// Projects
// *****************************************************************************

lazy val dateparser =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, GitBranchPrompt)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.scalaTest  % Test,
	    library.fastParse
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaTest  = "3.2.2"
      val fastParse  = "2.3.0"
    }
    val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val fastParse  = "com.lihaoyi" %% "fastparse" % Version.fastParse
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    organizationName := "asia.solutions",
    startYear := Some(2017),
    licenses += ("MIT", new URL("https://opensource.org/licenses/MIT")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
     // "-deprecation",
     // "-Xlint",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
      //"-Xmax-classfile-name", "100"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

