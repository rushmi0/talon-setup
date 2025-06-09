package commands

import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.runBlocking
import services.Environment
import services.DownloadSource

@OptIn(ExperimentalCli::class)
class SetupCommand : Subcommand("-i", "Initialize environment") {

    override fun execute() {
        Environment.initDir().fold(
            onSuccess = { message ->
                println(message)
            },
            onFailure = { error ->
                println("Error: ${error.message}")
            }
        )

        runBlocking {
            DownloadSource.downloadFileNative(Environment.talonDirPath)
        }
    }


}