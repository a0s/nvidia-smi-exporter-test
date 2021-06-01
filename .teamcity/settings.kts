import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.2"

project {
    println("-project--teamcity.build.branch=%teamcity.build.branch%")
    buildType(Build)
}

object MvnDeployRules {
    fun action(branch: String): String {
        println("-checkbox--teamcity.build.branch=%teamcity.build.branch%")
        return "false"
    }
}

object Build : BuildType({
    name = "Build"

    params {
        println("-params--teamcity.build.branch=%teamcity.build.branch%")

        text("BUILD_BRANCH", "%teamcity.build.branch%", allowEmpty = true)

        checkbox(
                "MVN_SKIP_TEST", MvnDeployRules.action("branch"),
                label = "IsMvnSkipTest",
                description = """MVN_SKIP_TEST: run --Dmaven.test.skip""",
                display = ParameterDisplay.PROMPT,
                checked = "true", unchecked = "false"
        )
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        dockerCommand {
            name = "DockerStep"

            conditions {
                println("-------%teamcity.build.branch%")
                equals("teamcity.build.branch", "release")
            }
            commandType = build {
                source = file {
                    path = "Dockerfile"
                }
                commandArgs = "--pull"
            }
        }
    }

    triggers {
        vcs {
        }
    }
})
