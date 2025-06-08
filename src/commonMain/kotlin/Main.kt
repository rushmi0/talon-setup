import commands.PlatformCommand
import kotlinx.cli.*


@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("kotlin-app-cli")

    parser.subcommands(
        PlatformCommand()
    )

    parser.parse(args)
}
