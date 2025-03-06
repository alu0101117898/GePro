package util.functions.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.TaskController
import data.CreateTaskData
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import model.task.Task
import model.user.toTaskUser
import util.functions.date.DatePickerDialog
import util.errorhandling.Result
import util.parseColor

@Composable
fun TaskDialog(
    listId: String,
    taskController: TaskController,
    task: Task? = null,
    teamMembers: List<model.User>,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    val dueDate by remember {
        mutableStateOf(
            task?.dueDate?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()
        )
    }

    var selectedAssignee by remember { mutableStateOf(task?.assignees?.firstOrNull()) }
    var status by remember { mutableStateOf(task?.status ?: "to do") }
    var dueDateTimestamp by remember { mutableStateOf(task?.dueDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }
    var showUserDropdown by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (task == null) "Nueva Tarea" else "Editar Tarea") },
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
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showUserDropdown = true }
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👤", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Asignado: " + (selectedAssignee?.username ?: "Sin asignar"),
                                style = MaterialTheme.typography.body1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(expanded = showUserDropdown, onDismissRequest = { showUserDropdown = false }) {
                        teamMembers.forEach { user ->
                            val defaultColor = if (user.color.isNullOrEmpty()) "#000000" else user.color
                            DropdownMenuItem(onClick = {
                                selectedAssignee = user.toTaskUser()
                                println(selectedAssignee)
                                showUserDropdown = false
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(parseColor(defaultColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.initials,
                                            style = MaterialTheme.typography.caption,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = user.username, style = MaterialTheme.typography.body2)
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStatusDropdown = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Estado: ${getEstadoText(status.toString())}", style = MaterialTheme.typography.body1)
                    Text(text = "Seleccionar", style = MaterialTheme.typography.button, color = MaterialTheme.colors.primary)
                }

                DropdownMenu(expanded = showStatusDropdown, onDismissRequest = { showStatusDropdown = false }) {
                    DropdownMenuItem(onClick = { status = "to do"; showStatusDropdown = false }) { Text("En proceso") }
                    DropdownMenuItem(onClick = { status = "complete"; showStatusDropdown = false }) { Text("Completada") }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val localDateTime =
                        dueDate.toLocalDateTime(TimeZone.currentSystemDefault())
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
                            val instant = selectedLocalDate
                                .atStartOfDayIn(TimeZone.UTC)
                                .toEpochMilliseconds()

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
                    val createTaskData = CreateTaskData(
                        name = name,
                        description = description,
                        dueDate = dueDateTimestamp ?: 0,
                        status = status.toString(),
                        assignees = selectedAssignee?.id?.let { listOf(it) }
                    )
                    coroutineScope.launch {
                        taskController.createTask(listId, createTaskData) { result ->
                            when (result) {
                                is Result.Success -> {
                                    onConfirm(result.data)
                                    onDismiss()
                                }
                                is Result.Error -> {
                                    println("Error al crear la tarea: ${result.error}")
                                }

                                Result.Loading -> {
                                    println("Cargando...")
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
}

@Composable
fun getEstadoText(status: String): String {
    return when (status) {
        "to do" -> "Sin Empezar"
        "complete" -> "Completada"
        else -> "Desconocido"
    }
}