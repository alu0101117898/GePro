package repository

import model.space.Space
import networking.SpaceClient
import networking.createHttpClient
import util.NetworkError
import util.Result

object SpaceRepository {
    private val client = SpaceClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getSpaces(teamId: String): Result<List<Space>, NetworkError> {
        return client.getSpaces(teamId)
    }
}
