package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.TaskRepository
import util.NetworkError
import util.Result
import data.TaskData
import model.Task

class TaskController(private val scope: CoroutineScope) {

    fun getTask(taskId: String, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.getTask(taskId)
            onResult(result)
        }
    }

    fun createTask(listId: String, taskData: TaskData, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.createTask(listId, taskData)
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
