package util.functions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Task

@Composable
fun UserStatusDialog(
    tasks: List<Task>,
    onDismiss: () -> Unit,
    userName: String
) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val animationProgress = remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress.value,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        animationProgress.value = 1f
    }
    val overdueTasksCount = tasks.count { task ->
        task.dueDate != null &&
                Instant.fromEpochMilliseconds(task.dueDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date < currentDate &&
                task.status?.status != "complete"
    }

    val dueTodayTasksCount = tasks.count { task ->
        task.dueDate != null &&
                Instant.fromEpochMilliseconds(task.dueDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date == currentDate &&
                task.status?.status != "complete"
    }

    val todoTasksCount = tasks.count { task ->
        task.status?.status == "to do" &&
                !(task.dueDate != null &&
                        (Instant.fromEpochMilliseconds(task.dueDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date < currentDate ||
                                Instant.fromEpochMilliseconds(task.dueDate)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date == currentDate) &&
                        task.status.status != "complete")
    }

    val inProgressTasksCount = tasks.count { it.status?.status == "in progress" }
    val completedTasksCount = tasks.count { it.status?.status == "complete" }

    val chartData = mapOf(
        "Retrasadas" to Pair(
            overdueTasksCount,
            Color(0xFFE57373)
        ),
        "Último día" to Pair(
            dueTodayTasksCount,
            Color(0xFFFFB74D)
        ),
        "Por hacer" to Pair(
            todoTasksCount,
            Color(0xFFBDBDBD)
        ),
        "En progreso" to Pair(
            inProgressTasksCount,
            Color(0xFF90CAF9)
        ),
        "Completadas" to Pair(
            completedTasksCount,
            Color(0xFFA5D6A7)
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Estado de tareas: $userName",
                style = MaterialTheme.typography.h6.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Total tareas: ${tasks.size}",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    AnimatedPieChart(
                        data = chartData,
                        progress = animatedProgress,
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ChartLegend(chartData)
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))

                val completionRate = if (tasks.isNotEmpty()) {
                    (completedTasksCount.toFloat() / tasks.size) * 100
                } else {
                    0f
                }

                Text(
                    "Tasa de finalización: ${String.format("%.1f", completionRate)}%",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )

                if (overdueTasksCount > 0) {
                    Text(
                        "Tareas retrasadas: $overdueTasksCount",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFE57373),
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
private fun AnimatedPieChart(
    data: Map<String, Pair<Int, Color>>,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val total = data.values.sumOf { it.first.toDouble() }
    var currentStartAngle = -90f

    Canvas(modifier = modifier.size(150.dp)) {
        data.forEach { (_, value) ->
            if (value.first > 0 && total > 0) {
                val fullSweep = (value.first / total * 360).toFloat()
                val animatedSweep = fullSweep * progress

                drawArc(
                    color = value.second,
                    startAngle = currentStartAngle + (fullSweep * (1 - progress)),
                    sweepAngle = animatedSweep,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                currentStartAngle += fullSweep
            }
        }
    }
}

@Composable
private fun ChartLegend(data: Map<String, Pair<Int, Color>>) {
    Column(modifier = Modifier.padding(4.dp)) {
        data.forEach { (label, value) ->
            if (value.first > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(value.second)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label: ${value.first}",
                        style = MaterialTheme.typography.caption.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}