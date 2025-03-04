package networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import util.errorhandling.NetworkError
import util.errorhandling.Result

class UsernameClient(private val httpClient: HttpClient) {
    suspend fun getUsername(): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/user") {
                header(HttpHeaders.Authorization, token)
            }

            val jsonResponse: JsonObject = response.body()
            val username = jsonResponse["user"]?.jsonObject?.get("username")?.toString()
                ?.replace("\"", "")
                ?: "Usuario Desconocido"

            val formattedUsername = username
                .lowercase()
                .split(" ")
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }

            Result.Success(formattedUsername)
        } catch (e: Exception) {
            println("Error al obtener el username: ${e.message}")
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}