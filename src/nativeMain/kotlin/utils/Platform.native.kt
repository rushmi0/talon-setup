package utils

import kotlin.native.OsFamily
import kotlin.native.Platform
import kotlin.experimental.ExperimentalNativeApi

class NativePlatform(val platformResult: Result<String>) : LocalPlatform {
    override val name: String = platformResult.getOrElse { "Error: ${it.message}" }
}

actual fun getPlatform(): LocalPlatform {
    return NativePlatform(getPlatformInfo())
}

@OptIn(ExperimentalNativeApi::class)
private fun getPlatformInfo(): Result<String> = runCatching {
    when (Platform.osFamily) {
        OsFamily.WINDOWS -> "Windows: ${Platform.cpuArchitecture}"
        OsFamily.LINUX -> "Linux: ${Platform.cpuArchitecture}"
        OsFamily.MACOSX -> "MacOS: ${Platform.cpuArchitecture}"
        else -> "Unknown OS (${Platform.osFamily})"
    }
}
