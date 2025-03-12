package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.list.TaskList
import repository.ListRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result

class ListController(private val scope: CoroutineScope) {

    fun getLists(folderId: String, onResult: (Result<List<TaskList>, NetworkError>) -> Unit) {
        scope.launch {
            val result = ListRepository.getLists(folderId)
            onResult(result)
        }
    }

    fun createList(spaceId: String, listName: String, onResult: (Result<TaskList, NetworkError>) -> Unit) {
        scope.launch {
            val result = ListRepository.createList(spaceId, listName)
            onResult(result)
        }
    }
}
