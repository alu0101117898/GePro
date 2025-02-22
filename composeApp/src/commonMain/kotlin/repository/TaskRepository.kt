package repository

import data.TaskData
import networking.TaskFunction
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.Task
import util.NetworkError
import util.Result

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

    suspend fun createTask(listId: String, taskData: TaskData): Result<Task, NetworkError> {
        return when (val result = client.createTask(listId, taskData)) {
            is Result.Success -> {
                try {
                    val task = Json.decodeFromString<Task>(result.data)
                    Result.Success(task)
                } catch (e: Exception) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }
    /*
    suspend fun updateTask(task: Task): Result<Task, NetworkError> {
        // Implementación para actualizar una tarea
    }

    suspend fun deleteTask(taskId: String): Result<Unit, NetworkError> {
        // Implementación para eliminar una tarea
    }*/
}
