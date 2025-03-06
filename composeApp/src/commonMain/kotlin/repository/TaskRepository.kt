package repository

import data.CreateTaskData
import data.UpdateTaskData
import networking.TaskFunction
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import model.task.Task
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig

object TaskRepository {
    private val client = TaskFunction(createHttpClient(CIO.create()))

    suspend fun createTask(listId: String, createTaskData: CreateTaskData): Result<Task, NetworkError> {
        return when (val result = client.createTask(listId, createTaskData)) {
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

    suspend fun updateTask(taskId: String, updateTaskData: UpdateTaskData): Result<Task, NetworkError> {
        return when (val result = client.updateTask(taskId, updateTaskData)) {
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
