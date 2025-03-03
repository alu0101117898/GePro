package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import util.errorhandling.NetworkError
import util.errorhandling.Result

// Supongamos que la respuesta JSON se mapea a esta data class:
data class UserResponse(
    val username: String
)

class UsernameClient(private val httpClient: HttpClient) {
    suspend fun getUsername(): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/user") {
                header(HttpHeaders.Authorization, token)
            }
            val userResponse: UserResponse = response.body()
            val formattedUsername = userResponse.username
                .lowercase()
                .split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
            Result.Success(formattedUsername)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}
