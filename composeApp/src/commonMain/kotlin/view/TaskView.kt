package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.DefaultIdsProvider
import controller.TaskController
import data.TaskData
import kotlinx.coroutines.launch
import model.task.Task
import util.errorhandling.Result

@Composable
fun TaskView(task: Task, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Task ID: ${task.id}")
        Text(text = "Task Name: ${task.name}")
    }
}

/*@Composable
fun CreateTaskView(taskController: TaskController) {
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    var defaultListId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            when (val result = DefaultIdsProvider.getDefaultListId()) {
                is Result.Success -> defaultListId = result.data
                is Result.Error -> message = "Error al obtener el ID de la lista: ${result.error}"
                Result.Loading -> {}
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de la Tarea") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción de la Tarea") }
        )

        Button(onClick = {
            if (name.isBlank()) {
                message = "El nombre de la tarea es obligatorio"
                return@Button
            }
            if (defaultListId == null) {
                message = "No se puede crear la tarea sin un ID de lista válido"
                return@Button
            }
            isLoading = true
            val taskData = TaskData(name = name, description = description)
            taskController.createTask(defaultListId!!, taskData) { result ->
                isLoading = false
                when (result) {
                    is Result.Success -> message = "¡Tarea creada exitosamente!"
                    is Result.Error -> message = "Error al crear la tarea: ${result.error}"
                    Result.Loading -> {}
                }
            }
        }) {
            Text("Crear Tarea")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(text = message)
    }
}

@Composable
fun UpdateTaskView(taskController: TaskController, task: Task, onBack: () -> Unit) {
    var name by remember { mutableStateOf(task.name) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de la Tarea") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción de la Tarea") }
        )

        Button(onClick = {
            isLoading = true
            val taskData = TaskData(name = name, description = description)
            task.id?.let {
                taskController.updateTask(it, taskData) { result ->
                    isLoading = false
                    when (result) {
                        is Result.Success -> {
                            message = "¡Tarea actualizada exitosamente!"
                        }

                        is Result.Error -> {
                            message = "Error al actualizar la tarea: ${result.error}"
                        }

                        else -> {}
                    }
                }
            }
        }) {
            Text("Actualizar Tarea")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(text = message)
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
@Composable
fun DeleteTaskView(taskController: TaskController, task: Task, onBack: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            isLoading = true
            task.id?.let {
                taskController.deleteTask(it) { result ->
                    isLoading = false
                    when (result) {
                        is Result.Success -> {
                            message = "¡Tarea eliminada exitosamente!"
                        }

                        is Result.Error -> {
                            message = "Error al eliminar la tarea: ${result.error}"
                        }

                        else -> {}
                    }
                }
            }
        }) {
            Text("Eliminar Tarea")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Text(text = message)
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
*/
