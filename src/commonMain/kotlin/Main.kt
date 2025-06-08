import commands.SetupCommand
import commands.PlatformCommand
import kotlinx.cli.*


@OptIn(ExperimentalCli::class)
fun run(args: Array<String>): Result<Unit> = runCatching {
    val parser = ArgParser("talon-cli")
    parser.subcommands(
        SetupCommand(),
        PlatformCommand()
    )
    parser.parse(args)
}


fun main(args: Array<String>) {
    run(args).onFailure { error ->
        println("Error: ${error.message}")
    }
}