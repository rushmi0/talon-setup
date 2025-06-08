package services

import kotlinx.cinterop.*
import platform.posix.*
import utils.flatMap
import utils.getPlatform

@OptIn(ExperimentalForeignApi::class)
object Environment {

    private val urls = listOf(
        "https://repo1.maven.org/maven2/fish/payara/distributions/payara-ml/6.2024.8/payara-ml-6.2024.8.zip",
        "https://dev.talon.jp/downloads/products/index.php?file=ProductNew_6_2_8.zip"
    )


    private val platform = getPlatform()

    /** Lazy-resolved current user's name, used once and reused */
    val username: String by lazy {
        val key = if (platform.name.startsWith("Windows")) "USERNAME" else "USER"
        getenv(key)?.toKString() ?: error("Unable to determine username.")
    }

    /** The full path to the .talon directory */
    val talonDirPath: String by lazy {
        if (platform.name.startsWith("Windows")) {
            "C:\\Users\\$username\\.talon"
        } else {
            "/home/$username/.talon"
        }
    }

    /** Initialize the .talon directory if needed */
    fun initDir(): Result<String> {
        return Result.success(talonDirPath).flatMap { path ->
            if (isDirReady(path)) {
                Result.success("Talon is ready at $path")
            } else {
                createTalonDir(path)
            }
        }
    }

    /** Check if the directory already exists */
    private fun isDirReady(path: String): Boolean = memScoped {
        val dir = opendir(path)
        if (dir != null) {
            closedir(dir)
            true
        } else false
    }

    /** Try to create the directory */
    private fun createTalonDir(path: String): Result<String> = memScoped {
        val result = mkdir(path)
        if (result == 0) {
            Result.success("Created .talon at $path")
        } else {
            Result.failure(Exception("Failed to create .talon at $path"))
        }
    }

}
