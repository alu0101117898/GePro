package view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Status
import model.task.Task
import util.formatDate


@Composable
fun TaskListForSpace(tasks: List<Task>) {
    Column(
        modifier = Modifier
            .padding(start = 32.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        tasks.forEach { task ->
            TaskListItem(task = task)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun TaskListItem(task: Task) {
    var showTaskMenu by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    val isDelayed = task.dueDate?.let { it < System.currentTimeMillis() } ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showTaskMenu = true },
        elevation = 2.dp,
        backgroundColor = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Nombre de la tarea
            Text(
                text = task.name,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Fila superior: Estado y Fecha
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Estado con estilo de "pastilla"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = if (isDelayed) Color.Red.copy(alpha = 0.1f) else Color.DarkGray.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isDelayed) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isDelayed) "Retrasada" else (task.status?.status ?: "Sin estado"),
                            color = if (isDelayed) Color.Red else getStatusColor(task.status),
                            style = MaterialTheme.typography.caption.copy(fontSize = 12.sp)
                        )
                    }
                }

                // Fecha formateada
                Text(
                    text = task.dueDate?.let { formatDate(it, currentYear) } ?: "Sin fecha",
                    style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
                    color = if (isDelayed) Color.Red else Color.Gray
                )
            }

            // Descripción con expansión
            task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                val showExpandButton = desc.length > 100 && !isExpanded

                Column {
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.body2.copy(color = Color.DarkGray),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showExpandButton) {
                        TextButton(
                            onClick = { isExpanded = true },
                            modifier = Modifier.padding(0.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    DropdownMenu(
        expanded = showTaskMenu,
        onDismissRequest = { showTaskMenu = false }
    ) {
        DropdownMenuItem(onClick = { /* TODO */ }) {
            Text("✏️ Editar")
        }
        DropdownMenuItem(onClick = { /* TODO */ }) {
            Text("🗑️ Eliminar", color = Color.Red)
        }
    }
}


fun getStatusColor(status: Status?): Color {
    return when (status?.status?.lowercase()) {
        "complete" -> Color(0xFF4CAF50)
        "in progress" -> Color(0xFF2196F3)
        "pendiente" -> Color(0xFFFF9800)
        else -> Color.DarkGray.copy(alpha = 0.8f)
    }
}