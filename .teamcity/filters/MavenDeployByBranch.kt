package filters

import java.io.File

fun mavenDeployByBranch(
        branch: String,
        _default_value: String,
        _package_value: String = "package",
        _deploy_value: String = "deploy",
        _deploy_branch_only: String = """
            +:master
        """.trimIndent()
): String {
    val list = _deploy_branch_only
            .split("\n")
            .map { it.removePrefix("+:") }
    File("/tmp/debug.out").writeText("----------------branch=${branch}")
    if (branch in list) {
        return _deploy_value
    } else {
        return _default_value
    }
}
