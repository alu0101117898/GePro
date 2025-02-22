package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Space
import repository.SpaceRepository
import util.NetworkError
import util.Result

class SpaceController(private val scope: CoroutineScope) {

    fun getSpaces(teamId: String, onResult: (Result<List<Space>, NetworkError>) -> Unit) {
        scope.launch {
            val result = SpaceRepository.getSpaces(teamId)
            onResult(result)
        }
    }
}
