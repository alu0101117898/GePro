package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import util.NetworkError
import util.Result

class ClickUpClient(private val httpClient: HttpClient) {

    suspend fun getClickUpTask(taskId: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/task/$taskId") {
                header(HttpHeaders.Authorization, "pk_152464594_FXIX0YM863JKD5OEPTXXGZDPJKAPQGDI")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            val taskData: String = response.body()
            Result.Success(taskData)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}