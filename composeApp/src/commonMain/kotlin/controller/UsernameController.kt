package controller

import data.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.UsernameRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result

class UsernameController(private val scope: CoroutineScope) {
    fun getUserInfo(onResult: (Result<UserInfo, NetworkError>) -> Unit) {
        scope.launch {
            val result = UsernameRepository.getUserInfo()
            onResult(result)
        }
    }
}
