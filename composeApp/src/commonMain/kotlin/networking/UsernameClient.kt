package networking

import data.UserDetails
import data.UserInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import util.errorhandling.NetworkError
import util.errorhandling.Result

class UsernameClient(private val httpClient: HttpClient) {
    suspend fun getUserInfo(): Result<UserInfo, NetworkError> {
        return try {
            val response: String = httpClient.get("https://api.clickup.com/api/v2/user") {
                header(HttpHeaders.Authorization, token)
            }.body()

            val jsonResponse = util.jsonConfig.parseToJsonElement(response).jsonObject
            val userJson = jsonResponse["user"]?.jsonObject
            if (userJson != null) {
                val rawUsername = userJson["username"]?.jsonPrimitive?.content ?: "Usuario Desconocido"
                val formattedUsername = rawUsername.lowercase().split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
                val id = userJson["id"]?.jsonPrimitive?.int ?: 0
                val email = userJson["email"]?.jsonPrimitive?.content ?: ""
                val color = userJson["color"]?.jsonPrimitive?.content
                val initials = userJson["initials"]?.jsonPrimitive?.content ?: rawUsername
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .take(2)

                val userDetails = UserDetails(
                    id = id,
                    username = rawUsername,
                    email = email,
                    color = color,
                    profilePicture = userJson["profilePicture"]?.jsonPrimitive?.content,
                    initials = initials
                )

                Result.Success(UserInfo(formattedUsername, userDetails))
            } else {
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            println("Error al obtener el usuario: ${e.message}")
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}
