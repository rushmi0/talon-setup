package services

import io.ktor.client.*
import io.ktor.client.engine.curl.Curl
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.readRemaining
import kotlinx.cinterop.*
import kotlinx.io.readByteArray
import platform.posix.*

object FileDownloader {

    private val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun saveToFile(fileName: String, path: String, data: ByteArray) {

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


    @OptIn(ExperimentalForeignApi::class)
    suspend fun downloadFileNative(locationPath: String) {
        val url = "https://repo1.maven.org/maven2/fish/payara/distributions/payara-ml/6.2024.8/payara-ml-6.2024.8.zip"

        val response: HttpResponse = client.get(url)
        val bytes: ByteArray = response.bodyAsChannel().readRemaining().readByteArray()
        val uri = Url(url)
        val fileName = uri.rawSegments.last()

        println("Filename from URL: $fileName")

        saveToFile(fileName, locationPath, bytes)

    }


}