package networking

import model.comment.CommentUpdateData
import model.comment.CommentUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.comment.Comment
import model.comment.CommentsResponse
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig
import util.token

class CommentClient(private val httpClient: HttpClient) {
    suspend fun getComments(taskId: String): Result<List<Comment>, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/task/$taskId/comment") {
                header(HttpHeaders.Authorization, token)
            }
            val responseBody: String = response.body()
            val commentsResponse = jsonConfig.decodeFromString<CommentsResponse>(responseBody)
            Result.Success(commentsResponse.comments)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getCurrentUser(): CommentUser? {
        return withContext(Dispatchers.IO) {
            try {
                val httpClient = createHttpClient(CIO.create())
                val response: String = httpClient.get("https://api.clickup.com/api/v2/user") {
                    header(HttpHeaders.Authorization, token)
                }.body()
                val jsonResponse = jsonConfig.parseToJsonElement(response).jsonObject
                val userJson = jsonResponse["user"]?.jsonObject
                if (userJson != null) {
                    val id = userJson["id"]?.jsonPrimitive?.int ?: 0
                    val username = userJson["username"]?.jsonPrimitive?.content ?: "Usuario Desconocido"
                    val email = userJson["email"]?.jsonPrimitive?.content ?: "unknown@example.com"
                    val initials = userJson["initials"]?.jsonPrimitive?.content
                        ?: username.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2)
                    CommentUser(id = id, username = username, email = email, initials = initials, profilePicture = null)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun createComment(taskId: String, commentData: CommentUpdateData): Result<String, NetworkError> {
        return try {
            val response = httpClient.post("https://api.clickup.com/api/v2/task/$taskId/comment") {
                header(HttpHeaders.Authorization, token)
                contentType(ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(commentData))
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

    suspend fun updateComment(commentId: String, commentData: CommentUpdateData): Result<String, NetworkError> {
        return try {
            val response = httpClient.put("https://api.clickup.com/api/v2/comment/$commentId") {
                header(HttpHeaders.Authorization, token)
                contentType(ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(commentData))
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

    suspend fun deleteComment(commentId: String): Result<Unit, NetworkError> {
        return try {
            val response = httpClient.delete("https://api.clickup.com/api/v2/comment/$commentId") {
                header(HttpHeaders.Authorization, token)
            }
            if (response.status.isSuccess()) {
                Result.Success(Unit)
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
