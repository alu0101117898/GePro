package util.functions.task.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.TaskController
import data.CommentUpdateData
import data.toTaskData
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Status
import model.task.Task
import util.errorhandling.Result
import util.functions.comment.CommentDialog
import util.functions.comment.EditCommentDialog
import util.functions.date.formatDate
import util.parseColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TaskListItem(
    task: Task,
    teamMembers: List<model.User>,
    taskController: TaskController,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    var showCompletionMessage by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf<List<data.Comment>>(emptyList()) }
    var editingComment by remember { mutableStateOf<data.Comment?>(null) }

    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueLocalDate = task.dueDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val coroutineScope = rememberCoroutineScope()
    val commentController = controller.CommentController(coroutineScope)

    val isDelayed = dueLocalDate != null && dueLocalDate < currentDate
    val isLastDay = dueLocalDate != null && dueLocalDate == currentDate

    var hovered by remember { mutableStateOf(false) }

    val hoverModifier = Modifier.onPointerEvent(
        PointerEventType.Move
    ) {
    }
        .onPointerEvent(PointerEventType.Enter) {
            hovered = true
        }
        .onPointerEvent(PointerEventType.Exit) {
            hovered = false
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color(0xFFF5F5F5)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                    task.assignees?.takeIf { it.isNotEmpty() }?.let { assignees ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Asignado a:",
                                style = MaterialTheme.typography.caption.copy(
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            assignees.forEach { user ->
                                val initials = user.initials ?: user.username.take(2).uppercase()
                                val color = parseColor(user.color ?: "#000000")

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(color, shape = CircleShape)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        color = Color.White,
                                        style = MaterialTheme.typography.caption,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = if (isDelayed) Color.Red.copy(alpha = 0.1f)
                                else Color.DarkGray.copy(alpha = 0.1f),
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
                                text = when {
                                    isDelayed -> "Retrasada"
                                    isLastDay -> "❗Último día"
                                    else -> task.status?.status ?: "Sin estado"
                                },
                                color = when {
                                    isDelayed -> Color.Red
                                    isLastDay -> Color(0xFFFFA000)
                                    else -> getStatusColor(task.status)
                                },
                                style = MaterialTheme.typography.caption.copy(fontSize = 12.sp)
                            )
                        }
                    }
                }

                task.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    val showExpandButton = desc.length > 100 && !isExpanded
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.body2.copy(color = Color.DarkGray),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (showExpandButton) {
                            TextButton(
                                onClick = { isExpanded = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("...", style = MaterialTheme.typography.body2.copy(color = Color.Gray))
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .then(hoverModifier)
            ) {
                Text(
                    text = "✏️",
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { showEditDialog = true }
                )
                Text(
                    text = "🗑️",
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { showDeleteDialog = true }
                )
                Text(
                    text = "💬",
                    fontSize = 10.sp,
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            task.id?.let { taskId ->
                                commentController.getComments(taskId) { result ->
                                    if (result is Result.Success) {
                                        comments = result.data
                                        showCommentsDialog = true
                                    } else {
                                        println("Error al obtener comentarios: $result")
                                    }
                                }
                            }
                        }
                    }
                )
            }
            val dateText = task.dueDate?.let {
                formatDate(it, currentDate)
            } ?: "Sin fecha"
            Text(
                text = dateText,
                style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
                color = if (isDelayed) Color.Red else Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }
    }

    if (showEditDialog) {
        TaskEditDialog(
            task = task,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTask ->
                task.id?.let { taskId ->
                    val taskData = updatedTask.toTaskData()
                    taskController.updateTask(taskId, taskData) { result ->
                        if (result is Result.Success) {
                            onTaskUpdated(result.data)
                            if (updatedTask.status?.status == "complete") {
                                showCompletionMessage = true
                            }
                        } else if (result is Result.Error) {
                            println("Error al actualizar la tarea: ${result.error}")
                        }
                    }
                }
                showEditDialog = false
            },
            taskController = taskController,
            teamMembers = teamMembers
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        task.id?.let { taskId ->
                            taskController.deleteTask(taskId) { result ->
                                if (result is Result.Success) {
                                    onTaskDeleted(task)
                                } else if (result is Result.Error) {
                                    println("Error al eliminar la tarea: ${result.error}")
                                }
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showCommentsDialog) {
        CommentDialog(
            comments = comments,
            onDismiss = { showCommentsDialog = false },
            onCreateComment = { commentText ->
                task.id?.let { taskId ->
                    coroutineScope.launch {
                        val commentData = CommentUpdateData(comment_text = commentText)
                        commentController.createComment(taskId, commentData) { result ->
                            if (result is Result.Success) {
                                val newComment = if (result.data.comment_text.isEmpty()) {
                                    result.data.copy(comment_text = commentText)
                                } else {
                                    result.data
                                }
                                comments = comments + newComment
                            } else {
                                println("Error al crear comentario: $result")
                            }
                        }
                    }
                }
            },
            onEditComment = { comment ->
                editingComment = comment
            },
            onDeleteComment = { comment ->
                coroutineScope.launch {
                    commentController.deleteComment(comment.id) { result ->
                        if (result is Result.Success) {
                            comments = comments.filter { it.id != comment.id }
                        } else {
                            println("Error al eliminar comentario: $result")
                        }
                    }
                }
            }
        )
    }

    val editingCommentId = editingComment?.id
    if (editingCommentId != null) {
        EditCommentDialog(
            comment = editingComment!!,
            onDismiss = { editingComment = null },
            onConfirm = { newText ->
                coroutineScope.launch {
                    val commentData = CommentUpdateData(comment_text = newText)
                    commentController.updateComment(editingCommentId, commentData) { result ->
                        if (result is Result.Success) {
                            val updatedComment = if (result.data.id.isEmpty()) {
                                result.data.copy(id = editingCommentId)
                            } else {
                                result.data
                            }
                            comments = comments.map { comment ->
                                if (comment.id == editingCommentId) updatedComment else comment
                            }
                        } else if (result is Result.Error) {
                            println("Error al actualizar comentario: ${result.error}")
                        }
                    }
                }
                editingComment = null
            }
        )
    }

}

fun getStatusColor(status: Status?): Color {
    return when (status?.status?.lowercase()) {
        "complete" -> Color(0xFF4CAF50)
        "to do" -> Color(0xFFFF9800)
        else -> Color.DarkGray.copy(alpha = 0.8f)
    }
}