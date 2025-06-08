package utils


interface LocalPlatform {
    val name: String
}

expect fun getPlatform(): LocalPlatform
