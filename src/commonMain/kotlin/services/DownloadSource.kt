package services

import io.ktor.client.*
import io.ktor.client.engine.curl.Curl
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import platform.posix.write
import kotlinx.cinterop.*
import kotlinx.io.readByteArray
import platform.posix.*
import platform.posix.open as posixOpen


object FileDownloader {

    private val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    suspend fun downloadFileNative(locationPath: String) {
        val url = "https://dev.talon.jp/downloads/products/index.php?file=ProductNew_6_2_8.zip"

        val response: HttpResponse = client.get(url)
        val bytes: ByteArray = response.bodyAsChannel().readRemaining().readByteArray()
        val uri = Url(url)
        val fileName = uri.parameters["file"]

        println("Filename from URL: $fileName")

        // Open file with write-only, create if not exists, truncate if exists, permission 0644
        val fd = memScoped {
            val cPathPtr = "$locationPath\\$fileName".cstr.getPointer(this).toString()
            posixOpen(cPathPtr, O_WRONLY or O_CREAT or O_TRUNC, 0b110100100)
        }

        if (fd == -1) {
            perror("❌ Failed to open file")
            return
        }

        // Write bytes to file
        bytes.usePinned { pinned ->
            val written = write(fd, pinned.addressOf(0), bytes.size.convert())
            if (written.toLong() == -1L) {
                perror("❌ Failed to write to file")
            } else {
                println("✅ Wrote $written bytes to $locationPath")
            }
        }

        // Close file
        close(fd)

        client.close()
    }


}