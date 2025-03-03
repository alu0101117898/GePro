package util.functions.spaces

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import controller.TaskController
import data.toTaskData
import model.space.Space
import model.task.Task
import util.errorhandling.NetworkError
import util.errorhandling.Result
import view.TaskListForSpace

@Composable
fun SpaceItem(
    space: Space,
    taskController: TaskController,
    onEdit: (Space) -> Unit,
    onDelete: (Space) -> Unit,
    onLoadTasks: (Space, (Result<List<Task>, NetworkError>) -> Unit) -> Unit
) {
    var tasksExpanded by remember { mutableStateOf(false) }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    var clickOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        clickOffset = offset
                        showPopup = true
                    }
                )
            }
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .animateContentSize(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = space.name,
                            style = MaterialTheme.typography.h6,
                            color = Color.Black
                        )
                    }
                    Icon(
                        imageVector = if (tasksExpanded)
                            Icons.Default.KeyboardArrowDown
                        else
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Mostrar/ocultar tareas",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                tasksExpanded = !tasksExpanded
                                if (tasksExpanded && tasks.isEmpty()) {
                                    onLoadTasks(space) { result ->
                                        if (result is Result.Success) {
                                            tasks = result.data
                                        }
                                    }
                                }
                            }
                    )
                }
                if (tasksExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskListForSpace(
                        tasks = tasks,
                    )
                }
            }
        }

        if (showPopup) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(clickOffset.x.toInt(), clickOffset.y.toInt()),
                onDismissRequest = { showPopup = false }
            ) {
                Card(
                    backgroundColor = Color.White,
                    elevation = 4.dp
                ) {
                    Column {
                        TextButton(onClick = {
                            showPopup = false
                            onEdit(space)
                        }) {
                            Text("Editar")
                        }
                        TextButton(onClick = {
                            showPopup = false
                            onDelete(space)
                        }) {
                            Text("Borrar", color = Color.Red)
                        }
                    }
                }
            }
        }

        taskToEdit?.let { task ->
            TaskEditDialog(
                task = task,
                onDismiss = { taskToEdit = null },
                onSave = { updatedTask ->
                    updatedTask.id?.let { s ->
                        taskController.updateTask(s, updatedTask.toTaskData()) { result ->
                            if (result is Result.Success) {
                                tasks = tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                            }
                        }
                    }
                    taskToEdit = null
                }
            )
        }
    }
}

@Composable
fun TaskEditDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var name by remember { mutableStateOf(task.name) }
    var description by remember { mutableStateOf(task.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarea") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(task.copy(name = name, description = description)) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

