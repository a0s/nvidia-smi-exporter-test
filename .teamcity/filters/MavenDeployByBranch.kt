package filters

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
    if (branch in list) {
        return _deploy_value
    } else {
        return _default_value
    }
}
