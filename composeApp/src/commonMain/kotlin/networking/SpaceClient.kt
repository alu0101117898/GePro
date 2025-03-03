package networking

import model.space.SpaceResponse
import data.SpaceData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.space.Space
import util.errorhandling.NetworkError
import util.errorhandling.Result

class SpaceClient(private val httpClient: HttpClient) {

    suspend fun getSpaces(teamId: String): Result<List<Space>, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/team/$teamId/space") {
                header(HttpHeaders.Authorization, token)
            }
            if (response.status.isSuccess()) {
                val spaceResponse: SpaceResponse = response.body()
                Result.Success(spaceResponse.spaces)
            } else {
                when (response.status.value) {
                    404 -> Result.Error(NetworkError.NOT_FOUND)
                    401 -> Result.Error(NetworkError.UNAUTHORIZED)
                    429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
                    else -> Result.Error(NetworkError.UNKNOWN)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun updateSpace(spaceId: String, spaceData: SpaceData): Result<String, NetworkError> {
        return try {
            val response = httpClient.put("https://api.clickup.com/api/v2/space/${spaceId}") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(Json.encodeToString(spaceData))
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
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun deleteSpace(spaceId: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.delete("https://api.clickup.com/api/v2/space/$spaceId") {
                header(HttpHeaders.Authorization, token)
            }
            if (response.status.isSuccess()) {
                Result.Success("Space deleted")
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
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun createSpace(teamId: String, spaceData: SpaceData): Result<String, NetworkError> {
        return try {
            val response = httpClient.post("https://api.clickup.com/api/v2/team/$teamId/space") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(Json.encodeToString(spaceData))
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
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}

