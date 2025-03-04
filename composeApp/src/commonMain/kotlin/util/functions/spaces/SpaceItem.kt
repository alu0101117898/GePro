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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
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
import data.TaskData
import data.toTaskData
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import model.space.Space
import model.task.Task
import util.date.DatePickerDialog
import util.errorhandling.NetworkError
import util.errorhandling.Result
import view.TaskListForSpace

@Composable
fun SpaceItem(
    space: Space,
    listId: String,
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
                    onPress = { offset ->
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
                .fillMaxWidth() // Ocupa el 80% del ancho disponible
                .widthIn(min = 300.dp) // Ancho mínimo de 300dp
                .animateContentSize(),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
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
                        listId = listId,
                        taskController = taskController,
                        onTaskUpdated = { updatedTask ->
                            tasks = tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                        },
                        onTaskDeleted = { deletedTask ->
                            tasks = tasks.filter { it.id != deletedTask.id }
                        },
                        onTaskCreated = { newTask ->
                            tasks = tasks + newTask
                        }
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
                },
                taskController = taskController

            )
        }
    }
}

@Composable
fun TaskEditDialog(
    task: Task,
    taskController: TaskController,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var name by remember { mutableStateOf(task.name) }
    var description by remember { mutableStateOf(task.description ?: "") }

    // Fecha límite: usamos dueDate para mostrar y dueDateTimestamp para almacenar el timestamp elegido.
    val initialDue = task.dueDate?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()
    val dueDate by remember { mutableStateOf(initialDue) }
    var dueDateTimestamp by remember { mutableStateOf(task.dueDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Estado: dos opciones: "to do" (Sin Empezar) y "complete" (Completada)
    var status by remember { mutableStateOf(task.status?.status ?: "to do") }
    var showStatusDropdown by remember { mutableStateOf(false) }

    // Para mostrar el mensaje de completado
    var showCompletionMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarea") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la tarea") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 5
                )

                // Menú para seleccionar el estado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatusDropdown = true }
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val displayStatus = if (status == "to do") "Sin Empezar" else "Completada"
                        Text(text = "Estado: $displayStatus", style = MaterialTheme.typography.body1)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(expanded = showStatusDropdown, onDismissRequest = { showStatusDropdown = false }) {
                        DropdownMenuItem(onClick = { status = "to do"; showStatusDropdown = false }) {
                            Text("Sin Empezar")
                        }
                        DropdownMenuItem(onClick = { status = "complete"; showStatusDropdown = false }) {
                            Text("Completada")
                        }
                    }
                }

                // Sección para seleccionar la fecha límite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val localDateTime = dueDate.toLocalDateTime(TimeZone.currentSystemDefault())
                    Text(
                        text = "Fecha: ${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = "Seleccionar",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )
                }
                if (showDatePicker) {
                    DatePickerDialog(
                        initialDate = dueDate.toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        onDateSelected = { selectedLocalDate ->
                            val instant = selectedLocalDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                            dueDateTimestamp = instant
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val taskData = TaskData(
                        name = name,
                        description = description,
                        dueDate = dueDateTimestamp ?: 0,
                        status = status
                    )
                    coroutineScope.launch {
                        task.id?.let { taskId ->
                            taskController.updateTask(taskId, taskData) { result ->
                                if (result is Result.Success) {
                                    onSave(result.data)
                                    if (status == "complete") {
                                        showCompletionMessage = true
                                    }
                                } else if (result is Result.Error) {
                                    println("Error al actualizar la tarea: ${result.error}")
                                }
                            }
                        }
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    if (showCompletionMessage) {
        AlertDialog(
            onDismissRequest = { showCompletionMessage = false },
            title = { Text("Tarea completada") },
            text = { Text("¡La tarea '$name' ha sido completada!") },
            confirmButton = {
                TextButton(onClick = { showCompletionMessage = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}



