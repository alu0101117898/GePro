package util.functions.task.observer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Task
import view.observer.formatDueDate


@Composable
fun ObserverTaskItem(task: Task) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueDate = task.dueDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val (statusText, statusColor) = when {
        task.status?.status == "complete" -> Pair("COMPLETADA", MaterialTheme.colors.primary.copy(alpha = 0.9f))
        dueDate == null -> Pair("PENDIENTE", MaterialTheme.colors.secondary.copy(alpha = 0.9f))
        dueDate < currentDate -> Pair("RETRASADA", Color(0xFFE57373))
        dueDate == currentDate -> Pair("ÚLTIMO DÍA", Color(0xFFFFD54F))
        else -> Pair("PENDIENTE", MaterialTheme.colors.secondary.copy(alpha = 0.9f))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = statusColor
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.caption.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            dueDate?.let {
                Text(
                    text = formatDueDate(it, currentDate),
                    style = MaterialTheme.typography.caption.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}