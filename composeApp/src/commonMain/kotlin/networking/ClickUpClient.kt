package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import util.NetworkError
import util.Result

const val token = "pk_152464594_FXIX0YM863JKD5OEPTXXGZDPJKAPQGDI"

class ClickUpClient(private val httpClient: HttpClient) {

    suspend fun getClickUpTask(taskId: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/task/$taskId") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            val taskData: String = response.body()
            Result.Success(taskData)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
    suspend fun postClickUpSpace(teamId: String, postData: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.post("https://api.clickup.com/api/v2/team/$teamId/space") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(postData)
            }
            if (response.status.isSuccess()) {
                val spaceData: String = response.body()
                Result.Success(spaceData)
            } else {
                Result.Error(NetworkError.CUSTOM)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}
@kotlinx.serialization.Serializable
data class ErrorResponse(val err: String, val ECODE: String)