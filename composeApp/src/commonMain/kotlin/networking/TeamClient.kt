package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import model.Team
import kotlinx.serialization.Serializable
import util.errorhandling.NetworkError
import util.errorhandling.Result


class TeamClient(private val httpClient: HttpClient) {

    suspend fun getTeams(): Result<List<Team>, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/team") {
                header(HttpHeaders.Authorization, token)
            }
            val teamResponse: TeamResponse = response.body()
            Result.Success(teamResponse.teams)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}

@Serializable
data class TeamResponse(
    val teams: List<Team>
)
