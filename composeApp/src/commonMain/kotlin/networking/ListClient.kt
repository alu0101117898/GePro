package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.isSuccess
import model.TaskList
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig

class ListClient(private val httpClient: HttpClient) {

    suspend fun getLists(spaceId: String): Result<List<TaskList>, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/space/$spaceId/list") {
                header(HttpHeaders.Authorization, token)
            }
            val listResponse: ListResponse = response.body()
            Result.Success(listResponse.lists)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun createList(spaceId: String, listName: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.post("https://api.clickup.com/api/v2/space/$spaceId/list") {
                header(HttpHeaders.Authorization, token)
                header(ContentType, io.ktor.http.ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(mapOf("name" to listName)))
            }
            if (response.status.isSuccess()) {
                val responseData: String = response.body()
                Result.Success(responseData)
            } else {
                when (response.status.value) {
                    400 -> Result.Error(NetworkError.BAD_REQUEST)
                    401 -> Result.Error(NetworkError.UNAUTHORIZED)
                    403 -> Result.Error(NetworkError.FORBIDDEN)
                    404 -> Result.Error(NetworkError.NOT_FOUND)
                    429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
                    else -> Result.Error(NetworkError.UNKNOWN)
                }
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}

@Serializable
data class ListResponse(
    val lists: List<TaskList>
)
