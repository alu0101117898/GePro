package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.Task
import model.TaskRepository
import util.NetworkError
import util.Result

class TaskController(private val scope: CoroutineScope) {

    fun getTask(taskId: String, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.getTask(taskId)
            onResult(result)
        }
    }

    fun createTask(task: Task, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.createTask(task)
            onResult(result)
        }
    }
    /*
    fun updateTask(task: Task, onResult: (Result<Unit, String>) -> Unit) {
        scope.launch {
            val result = TaskRepository.updateTask(task)
            onResult(result)
        }
    }

    fun deleteTask(taskId: String, onResult: (Result<Unit, String>) -> Unit) {
        scope.launch {
            val result = TaskRepository.deleteTask(taskId)
            onResult(result)
        }
    }*/
}
