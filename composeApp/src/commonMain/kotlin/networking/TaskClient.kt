package networking

import model.task.CreateTaskData
import model.task.UpdateTaskData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig
import util.token

class TaskFunction(private val httpClient: HttpClient) {
    suspend fun createTask(listId: String, createTaskData: CreateTaskData): Result<String, NetworkError> {
        return try {
            val response: HttpResponse = httpClient.post("https://api.clickup.com/api/v2/list/$listId/task") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(createTaskData))
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


    suspend fun updateTask(taskId: String, updateTaskData: UpdateTaskData): Result<String, NetworkError> {
        return try {
            val response: HttpResponse = httpClient.put("https://api.clickup.com/api/v2/task/$taskId") {
                header(HttpHeaders.Authorization, token)
                contentType(ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(updateTaskData))
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

    suspend fun deleteTask(taskId: String): Result<Unit, NetworkError> {
        return try {
            val response: HttpResponse = httpClient.delete("https://api.clickup.com/api/v2/task/$taskId") {
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
            e.printStackTrace()
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getTasks(listId: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/list/$listId/task?include_closed=true&Key:statuses[]=complete") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val taskData: String = response.body()
                Result.Success(taskData)
            } else {
                when (response.status.value) {
                    404 -> Result.Error(NetworkError.NOT_FOUND)
                    401 -> Result.Error(NetworkError.UNAUTHORIZED)
                    429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
                    else -> Result.Error(NetworkError.UNKNOWN)
                }
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}