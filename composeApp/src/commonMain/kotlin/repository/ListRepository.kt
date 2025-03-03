package repository

import model.TaskList
import networking.ListClient
import networking.createHttpClient
import util.errorhandling.NetworkError
import util.errorhandling.Result

object ListRepository {
    private val client = ListClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getLists(spaceId: String): Result<List<TaskList>, NetworkError> {
        return client.getLists(spaceId)
    }
}
