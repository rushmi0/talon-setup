package commands

import utils.getPlatform
import kotlinx.cli.*

@OptIn(ExperimentalCli::class)
class PlatformCommand : Subcommand("-os", "Show platform information") {
    override fun execute() {
        val platform = getPlatform()
        println("Running on platform: ${platform.name}")
    }
}
