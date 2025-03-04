package repository

import networking.UsernameClient
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import util.errorhandling.NetworkError
import util.errorhandling.Result

object UsernameRepository {
    private val client = UsernameClient(createHttpClient(CIO.create()))

    suspend fun getUsername(): Result<String, NetworkError> {
        return try {
            client.getUsername()
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}