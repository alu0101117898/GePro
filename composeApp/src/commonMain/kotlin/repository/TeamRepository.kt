package repository

import model.Team
import networking.TeamClient
import networking.createHttpClient
import util.NetworkError
import util.Result

object TeamRepository {
    private val client = TeamClient(createHttpClient(io.ktor.client.engine.cio.CIO.create()))

    suspend fun getTeams(): Result<List<Team>, NetworkError> {
        return client.getTeams()
    }
}
