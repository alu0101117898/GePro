package repository

import data.UserInfo
import networking.UsernameClient
import networking.createHttpClient
import io.ktor.client.engine.cio.*
import util.errorhandling.NetworkError
import util.errorhandling.Result

object UsernameRepository {
    private val client = UsernameClient(createHttpClient(CIO.create()))

    suspend fun getUserInfo(): Result<UserInfo, NetworkError> {
        return try {
            client.getUserInfo()
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}