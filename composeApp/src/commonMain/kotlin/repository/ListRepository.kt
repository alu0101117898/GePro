package repository

import model.TaskList
import networking.ListClient
import networking.createHttpClient
import util.NetworkError
import util.Result

object ListRepository {
    private val client = ListClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getLists(folderId: String): Result<List<TaskList>, NetworkError> {
        return client.getLists(folderId)
    }
}
