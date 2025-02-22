package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Team
import repository.TeamRepository
import util.NetworkError
import util.Result

class TeamController(private val scope: CoroutineScope) {

    fun getTeams(onResult: (Result<List<Team>, NetworkError>) -> Unit) {
        scope.launch {
            val result = TeamRepository.getTeams()
            onResult(result)
        }
    }
}
