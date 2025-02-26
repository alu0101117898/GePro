package repository

import data.SpaceData
import model.space.Space
import networking.SpaceClient
import networking.createHttpClient
import util.NetworkError
import util.Result
import util.jsonConfig

object SpaceRepository {
    private val client = SpaceClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getSpaces(teamId: String): Result<List<Space>, NetworkError> {
        return client.getSpaces(teamId)
    }

    suspend fun updateSpace(spaceId: String, spaceData: SpaceData): Result<Space, NetworkError> {
        return when (val result = client.updateSpace(spaceId, spaceData)) {
            is Result.Success -> {
                try {
                    val space = jsonConfig.decodeFromString<Space>(result.data)
                    Result.Success(space)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            is Result.Error -> Result.Error(result.error)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun deleteSpace(spaceId: String): Result<Unit, NetworkError> {
        return when (val result = client.deleteSpace(spaceId)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(result.error)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}
