package util.functions.space

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Task

@Composable
fun SpaceStatusDialog(
    tasks: List<Task>,
    onDismiss: () -> Unit,
    spaceName: String
) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val chartData = mapOf(
        "Retrasadas" to Pair(
            tasks.count { task ->
                task.dueDate != null &&
                        Instant.fromEpochMilliseconds(task.dueDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date < currentDate &&
                        task.status?.status != "complete"
            },
            Color(0xFFE57373)
        ),
        "Último día" to Pair(
            tasks.count { task ->
                task.dueDate != null &&
                        Instant.fromEpochMilliseconds(task.dueDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date == currentDate &&
                        task.status?.status != "complete"
            },
            Color(0xFFFFB74D)
        ),
        "Sin empezar" to Pair(
            tasks.count { it.status?.status == "to do" },
            Color(0xFFBDBDBD)
        ),
        "Completadas" to Pair(
            tasks.count { it.status?.status == "complete" },
            Color(0xFFA5D6A7)
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Estado de $spaceName", style = MaterialTheme.typography.h6) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total tareas: ${tasks.size}",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    PieChart(
                        data = chartData,
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ChartLegend(chartData)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", style = MaterialTheme.typography.button)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
private fun PieChart(
    data: Map<String, Pair<Int, Color>>, // Tipo correcto
    modifier: Modifier = Modifier
) {
    val total = data.values.sumOf { it.first.toDouble() }
    var startAngle = -90f

    Canvas(modifier = modifier.size(150.dp)) {
        data.forEach { (_, value) ->
            if (value.first > 0) {
                val sweepAngle = (value.first / total * 360).toFloat()
                drawArc(
                    color = value.second,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
private fun ChartLegend(data: Map<String, Pair<Int, Color>>) {
    Column(modifier = Modifier.padding(4.dp)) {
        data.forEach { (label, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(value.second))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$label: ${value.first}",
                    style = MaterialTheme.typography.caption,
                    fontSize = 12.sp)
            }
        }
    }
}