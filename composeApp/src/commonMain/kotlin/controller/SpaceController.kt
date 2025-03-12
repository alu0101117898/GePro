package controller

import model.space.SpaceData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.space.Space
import model.space.toSpaceData
import repository.SpaceRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result

class SpaceController(private val scope: CoroutineScope) {
    fun getSpaces(teamId: String, onResult: (Result<List<Space>, NetworkError>) -> Unit) {
        scope.launch {
            val result = SpaceRepository.getSpaces(teamId)
            onResult(result)
        }
    }

    fun updateSpace(space: Space, onResult: (Result<Space, NetworkError>) -> Unit) {
        scope.launch {
            val spaceData = space.toSpaceData()
            val result = SpaceRepository.updateSpace(space.id, spaceData)
            onResult(result)
        }
    }

    fun deleteSpace(spaceId: String, onResult: (Result<Unit, NetworkError>) -> Unit) {
        scope.launch {
            val result = SpaceRepository.deleteSpace(spaceId)
            onResult(result)
        }
    }

    fun createSpace(teamId: String, spaceData: SpaceData, onResult: (Result<Space, NetworkError>) -> Unit) {
        scope.launch {
            val result = SpaceRepository.createSpace(teamId, spaceData)
            onResult(result)
        }
    }
}
