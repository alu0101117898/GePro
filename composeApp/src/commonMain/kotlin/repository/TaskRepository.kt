package repository

import data.TaskData
import networking.TaskFunction
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.task.Task
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig

object TaskRepository {
    private val client = TaskFunction(createHttpClient(CIO.create()))

    suspend fun getTask(taskId: String): Result<Task, NetworkError> {
        return when (val result = client.getTask(taskId)) {
            is Result.Success -> {
                try {
                    val json = jsonConfig.decodeFromString<JsonObject>(result.data)
                    val name = json["name"]?.jsonPrimitive?.content ?: "Unknown"
                    Result.Success(Task(id = taskId, name = name))
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
                    val task = jsonConfig.decodeFromString<Task>(result.data)
                    Result.Success(task)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun updateTask(taskId: String, taskData: TaskData): Result<Task, NetworkError> {
        return when (val result = client.updateTask(taskId, taskData)) {
            is Result.Success -> {
                try {
                    val task = jsonConfig.decodeFromString<Task>(result.data)
                    Result.Success(task)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit, NetworkError> {
        return client.deleteTask(taskId)
    }

    // TaskRepository.kt
    suspend fun getTasks(listId: String): Result<List<Task>, NetworkError> {
        return when (val result = client.getTasks(listId)) {
            is Result.Success -> {
                try {
                    val json = jsonConfig.parseToJsonElement(result.data)
                    val tasksArray = json.jsonObject["tasks"]?.jsonArray ?: throw Exception("Campo 'tasks' no encontrado")

                    val tasks = tasksArray.map { taskJson ->
                        jsonConfig.decodeFromJsonElement(Task.serializer(), taskJson)
                    }

                    Result.Success(tasks)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}
