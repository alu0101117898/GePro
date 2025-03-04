package controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.TaskRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result
import data.TaskData
import model.task.Task

class TaskController(private val scope: CoroutineScope) {

    fun createTask(listId: String, taskData: TaskData, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.createTask(listId, taskData)
            onResult(result)
        }
    }


    fun updateTask(taskId: String, taskData: TaskData, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.updateTask(taskId, taskData)
            onResult(result)
        }
    }

    fun deleteTask(taskId: String, onResult: (Result<Unit, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.deleteTask(taskId)
            onResult(result)
        }
    }
    fun getTasks(listId: String, onResult: (Result<List<Task>, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.getTasks(listId)
            onResult(result)
        }
    }
}
