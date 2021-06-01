import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import filters.mavenDeployByBranch

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
    subProject(SubP)

    params {
        checkbox("CUSTOM_CHECKBOX_PREV", "deploy",
                label = "label",
                description = "description",
                display = ParameterDisplay.PROMPT,
                checked = "deploy",
                unchecked = "package")

        param("CUSTOM_CHECKBOX", mavenDeployByBranch(
                "%teamcity.build.branch%",
                _default_value = "%CUSTOM_CHECKBOX_PREV%"))
    }
}

object SubP : Project({
    name = "sub_project"

    buildType(SubPBuild)
})

object SubPBuild : BuildType({
    name = "sub_build"

    steps {
        script {
            name = "script"

            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux

            scriptContent = """
                    #!/usr/bin/env bash
                    set -e
                    echo "----CUSTOM_CHECKBOX=>>>%CUSTOM_CHECKBOX%<<<"                                        
                """.trimIndent()
        }
    }

    vcs {
        root(DslContext.settingsRoot)
    }
})

