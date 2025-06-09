package services

import io.ktor.client.*
import io.ktor.client.engine.curl.Curl
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import services.FileControl.saveFile
import services.FileControl.unzipFile

object DownloadSource {

    private val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }

    suspend fun downloadFileNative(locationPath: String) {
        val url = "https://repo1.maven.org/maven2/fish/payara/distributions/payara-ml/6.2024.8/payara-ml-6.2024.8.zip"

        val response: HttpResponse = client.get(url)
        val bytes: ByteArray = response.bodyAsChannel().readRemaining().readByteArray()
        val uri = Url(url)
        val fileName = uri.rawSegments.last()

        println("📦 Downloading: $fileName to $locationPath")

        saveFile(fileName, locationPath, bytes)

        val separator = if (locationPath.endsWith('/') || locationPath.endsWith('\\')) "" else "/"
        val zipPath = "$locationPath$separator$fileName"

        unzipFile(zipPath, locationPath).fold(
            onSuccess = { println("✅ Unzipped successfully to $locationPath") },
            onFailure = { println("❌ Failed to unzip: ${it.message}") }
        )
    }
}
