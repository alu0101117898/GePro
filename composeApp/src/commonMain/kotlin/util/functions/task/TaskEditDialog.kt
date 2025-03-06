package util.functions.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.TaskController
import data.TaskData
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import model.task.Task
import util.errorhandling.Result
import util.functions.date.DatePickerDialog

@Composable
fun TaskEditDialog(
    task: Task,
    taskController: TaskController,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var name by remember { mutableStateOf(task.name) }
    var description by remember { mutableStateOf(task.description ?: "") }

    val initialDue = task.dueDate?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()
    val dueDate by remember { mutableStateOf(initialDue) }
    var dueDateTimestamp by remember { mutableStateOf(task.dueDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    var status by remember { mutableStateOf(task.status?.status ?: "to do") }
    var showStatusDropdown by remember { mutableStateOf(false) }

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