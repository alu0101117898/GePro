package repository

import model.TaskList
import networking.ListClient
import networking.createHttpClient
import util.errorhandling.NetworkError
import util.errorhandling.Result
import util.jsonConfig

object ListRepository {
    private val client = ListClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getLists(spaceId: String): Result<List<TaskList>, NetworkError> {
        return client.getLists(spaceId)
    }

    suspend fun createList(spaceId: String, listName: String): Result<TaskList, NetworkError> {
        return when (val result = client.createList(spaceId, listName)) {
            is Result.Success -> {
                try {
                    val taskList = jsonConfig.decodeFromString(TaskList.serializer(), result.data)
                    Result.Success(taskList)
                } catch (e: Exception) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            Result.Loading -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}
