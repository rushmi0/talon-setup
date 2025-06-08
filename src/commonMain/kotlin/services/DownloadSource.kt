package services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.curl.Curl
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.utils.io.core.use
import io.ktor.utils.io.readAvailable
import kotlinx.io.files.Path
import services.Environment.talonDirPath


object FileDownloader {

    private val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }

    private val urls = listOf(
        "https://repo1.maven.org/maven2/fish/payara/distributions/payara-ml/6.2024.8/payara-ml-6.2024.8.zip",
        "https://dev.talon.jp/downloads/products/index.php?file=ProductNew_6_2_8.zip"
    )

    /*suspend fun downloadFile(url: String): Result<Unit> = runCatching {
        val response = client.get(url).bodyAsChannel()
        val fileName = url.substringAfterLast("/")
        val filePath = Path(talonDirPath).resolve(fileName)

        // Open file sink (write)
        FileSink(filePath).use { sink ->
            val buffer = ByteArray(8 * 1024)
            while (!response.isClosedForRead) {
                val read = response.readAvailable(buffer, 0, buffer.size)
                if (read > 0) {
                    sink.write(buffer, 0, read)
                }
            }
        }

        println("Downloaded: $filePath")
    }*/

    /*suspend fun downloadAll(): List<Result<Unit>> {
        return urls.map { downloadFile(it) }
    }*/
}
