package networking

import data.TaskData
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

const val token = "pk_152464594_FXIX0YM863JKD5OEPTXXGZDPJKAPQGDI"

class TaskFunction(private val httpClient: HttpClient) {

    suspend fun getTask(taskId: String): Result<String, NetworkError> {
        return try {
            val response = httpClient.get("https://api.clickup.com/api/v2/task/$taskId") {
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
    suspend fun createTask(listId: String, taskData: TaskData): Result<String, NetworkError> {
        return try {
            println("Intentando crear tarea en ClickUp. listId: $listId")

            val response: HttpResponse = httpClient.post("https://api.clickup.com/api/v2/list/$listId/task") {
                header(HttpHeaders.Authorization, token)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(taskData))
            }

            if (response.status.isSuccess()) {
                val responseData: String = response.body()
                Result.Success(responseData)
            } else {
                println("Error en la respuesta de ClickUp: ${response.status}") // LOG para depurar
                when (response.status.value) {
                    400 -> Result.Error(NetworkError.BAD_REQUEST)
                    401 -> Result.Error(NetworkError.UNAUTHORIZED) // Token incorrecto
                    403 -> Result.Error(NetworkError.FORBIDDEN) // Sin permisos en el equipo
                    404 -> Result.Error(NetworkError.NOT_FOUND) // List ID incorrecto
                    429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS) // Límite de API alcanzado
                    else -> Result.Error(NetworkError.UNKNOWN)
                }
            }
        } catch (e: Exception) {
            println("Excepción al crear tarea: ${e.message}") // LOG para depurar
            Result.Error(NetworkError.UNKNOWN)
        }
    }


    suspend fun updateTask(taskId: String, taskData: TaskData): Result<String, NetworkError> {
        return try {
            val response: HttpResponse = httpClient.put("https://api.clickup.com/api/v2/task/$taskId") {
                header(HttpHeaders.Authorization, token)
                contentType(ContentType.Application.Json)
                setBody(jsonConfig.encodeToString(taskData))
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
            val response = httpClient.get("https://api.clickup.com/api/v2/list/$listId/task") {
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