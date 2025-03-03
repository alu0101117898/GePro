package model.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import util.DatePickerDialog

@Composable
fun TaskDialog(
    task: Task? = null,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var dueDate by remember {
        mutableStateOf(
            task?.dueDate?.let { Instant.fromEpochMilliseconds(it) } ?: Clock.System.now()
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

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
                // Sección para mostrar y seleccionar la fecha
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
                            // Convertimos el LocalDate a Instant (inicio del día)
                            dueDate = selectedLocalDate.atStartOfDayIn(TimeZone.currentSystemDefault())
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
                    onConfirm(
                        Task(
                            id = task?.id ?: "",
                            name = name,
                            description = description,
                            dueDate = dueDate.toEpochMilliseconds(),
                            status = task?.status,
                        )
                    )
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
