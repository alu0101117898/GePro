package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import model.space.Space
import kotlinx.serialization.Serializable
import util.NetworkError
import util.Result

class SpaceClient(private val httpClient: HttpClient) {

    suspend fun getSpaces(teamId: String): Result<List<Space>, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/team/$teamId/space") {
                header(HttpHeaders.Authorization, token)
            }
            val spaceResponse: SpaceResponse = response.body()
            Result.Success(spaceResponse.spaces)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}

@Serializable
data class SpaceResponse(
    val spaces: List<Space>
)
