package controller

import data.CreateTaskData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.TaskRepository
import util.errorhandling.NetworkError
import util.errorhandling.Result
import data.UpdateTaskData
import model.task.Task

class TaskController(private val scope: CoroutineScope) {

    fun createTask(listId: String, createTaskData: CreateTaskData, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.createTask(listId, createTaskData)
            onResult(result)
        }
    }


    fun updateTask(taskId: String, updateTaskData: UpdateTaskData, onResult: (Result<Task, NetworkError>) -> Unit) {
        scope.launch {
            val result = TaskRepository.updateTask(taskId, updateTaskData)
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
