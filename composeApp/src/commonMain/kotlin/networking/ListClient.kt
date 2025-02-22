package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import model.TaskList
import kotlinx.serialization.Serializable
import util.NetworkError
import util.Result

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
}

@Serializable
data class ListResponse(
    val lists: List<TaskList>
)
