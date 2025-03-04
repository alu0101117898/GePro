package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.UsernameRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result

class UsernameController(private val scope: CoroutineScope) {

    fun getUsername(onResult: (Result<String, NetworkError>) -> Unit) {
        scope.launch {
            val result = UsernameRepository.getUsername()
            onResult(result)
        }
    }
}