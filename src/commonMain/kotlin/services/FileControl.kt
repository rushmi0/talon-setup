package services

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.posix.perror
import platform.posix.system
import utils.getPlatform


@OptIn(ExperimentalForeignApi::class)
object FileControl {


    fun unzipFile(zipFilePath: String, outputDir: String): Result<Unit> {
        val platformName = getPlatform().name

        val command = if (platformName.contains("Windows", ignoreCase = true)) {
            """powershell -Command "Expand-Archive -Path '$zipFilePath' -DestinationPath '$outputDir' -Force""""
        } else {
            """unzip -o "$zipFilePath" -d "$outputDir""""
        }

        println("Running unzip command:\n$command")

        return runCatching {
            val exitCode = system(command)
            if (exitCode != 0) {
                throw RuntimeException("Unzip failed with code $exitCode")
            }
        }
    }



    fun saveFile(fileName: String, path: String, data: ByteArray) {

        // ถ้า path ลงท้ายด้วย / หรือ \ แล้ว ก็เอา path กับ fileName มาต่อกันตรง ๆ
        // ถ้าไม่ ก็เติม "/" เข้าไปเลย (ใช้ / ได้ทั้งบน Unix และ Windows)
        val separator = if (path.endsWith('/') || path.endsWith('\\')) "" else "/"

        val file = fopen("$path$separator$fileName", "wb")
        if (file == null) {
            perror("Failed to open file")
            return
        }

        data.usePinned { pinned ->
            fwrite(
                pinned.addressOf(0),
                1.convert(),
                data.size.convert(),
                file
            )
        }

        fclose(file)
    }


}