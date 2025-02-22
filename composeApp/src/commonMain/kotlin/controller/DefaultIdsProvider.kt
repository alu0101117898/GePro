package controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import repository.*
import util.NetworkError
import util.Result

object DefaultIdsProvider {

    private var teamId: String? = null
    private var spaceId: String? = null
    private var listId: String? = null

    suspend fun getDefaultListId(): Result<String, NetworkError> {
        return withContext(Dispatchers.IO) {
            try {
                if (listId != null) {
                    return@withContext Result.Success(listId!!)
                }

                val teamsResult = TeamRepository.getTeams()
                if (teamsResult is Result.Success && teamsResult.data.isNotEmpty()) {
                    teamId = teamsResult.data.first().id
                } else {
                    return@withContext Result.Error(NetworkError.NOT_FOUND)
                }

                val spacesResult = SpaceRepository.getSpaces(teamId!!)
                if (spacesResult is Result.Success && spacesResult.data.isNotEmpty()) {
                    spaceId = spacesResult.data.first().id
                } else {
                    return@withContext Result.Error(NetworkError.NOT_FOUND)
                }

                val listsResult = ListRepository.getLists(spaceId!!)
                if (listsResult is Result.Success && listsResult.data.isNotEmpty()) {
                    listId = listsResult.data.first().id
                    Result.Success(listId!!)
                } else {
                    Result.Error(NetworkError.NOT_FOUND)
                }
            } catch (e: Exception) {
                Result.Error(NetworkError.UNKNOWN)
            }
        }
    }
}
