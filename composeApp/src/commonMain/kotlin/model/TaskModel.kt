package model

import networking.TaskFunction
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import util.NetworkError
import util.Result

data class Task(val id: String, val name: String, val status: String)

object TaskRepository {
    private val client = TaskFunction(createHttpClient(CIO.create()))

    suspend fun getTask(taskId: String): Result<Task, NetworkError> {
        return when (val result = client.getTask(taskId)) {
            is Result.Success -> {
                try {
                    val json = Json.decodeFromString<JsonObject>(result.data)
                    val name = json["name"]?.jsonPrimitive?.content ?: "Unknown"
                    val status = json["status"]?.jsonObject?.get("status")?.jsonPrimitive?.content ?: "Unknown"
                    Result.Success(Task(id = taskId, name = name, status = status))
                } catch (e: Exception) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun createTask(task: Task): Result<Task, NetworkError> {

        return Result.Error(NetworkError.UNKNOWN)
    }
    /*
    suspend fun updateTask(task: Task): Result<Task, NetworkError> {
        // Implementación para actualizar una tarea
    }

    suspend fun deleteTask(taskId: String): Result<Unit, NetworkError> {
        // Implementación para eliminar una tarea
    }*/
}
