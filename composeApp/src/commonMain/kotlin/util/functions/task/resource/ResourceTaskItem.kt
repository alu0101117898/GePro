package util.functions.task.resource

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import controller.CommentController
import controller.TaskController
import data.CommentUpdateData
import data.toTaskData
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.task.Task
import util.functions.comment.CommentDialog
import util.errorhandling.Result
import util.functions.comment.EditCommentDialog
import util.functions.date.formatDate

@Composable
fun ResourceTaskItem(
    task: Task,
    currentUserId: Int,
    taskController: TaskController,
    onTaskStateChanged: (Task) -> Unit
) {
    var showCommentsDialog by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf(emptyList<data.Comment>()) }
    var taskStatus by remember { mutableStateOf(task.status?.status ?: "to do") }
    val coroutineScope = rememberCoroutineScope()
    val commentController = remember { CommentController(coroutineScope) }
    var editingComment by remember { mutableStateOf<data.Comment?>(null) }
    var showPermissionErrorDialog by remember { mutableStateOf(false) }


    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueLocalDate = task.dueDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val isDelayed = dueLocalDate != null && dueLocalDate < currentDate && taskStatus != "complete"
    val isLastDay = dueLocalDate != null && dueLocalDate == currentDate && taskStatus != "complete"

    val statusColor = when {
        isDelayed -> Color(0xFFE57373)
        isLastDay -> Color(0xFFFFD54F)
        taskStatus == "complete" -> MaterialTheme.colors.primary.copy(alpha = 0.9f)
        else -> MaterialTheme.colors.secondary.copy(alpha = 0.9f)
    }

    val statusText = when {
        isDelayed -> "RETRASADA"
        isLastDay -> "ÚLTIMO DÍA!"
        taskStatus == "complete" -> "COMPLETADA"
        else -> "PENDIENTE"
    }

    val updateTaskStatusAndUI = { newStatus: String ->
        taskStatus = newStatus

        val updatedTask = task.toTaskData().copy(status = newStatus)
        coroutineScope.launch {
            task.id?.let { taskId ->
                taskController.updateTask(taskId, updatedTask) { result ->
                    if (result is Result.Success) {
                        onTaskStateChanged(result.data)
                    } else {
                        taskStatus = task.status?.status ?: "to do"
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 8.dp,
        backgroundColor = statusColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                StatusIndicatorChip(statusText)
            }

            Spacer(modifier = Modifier.height(12.dp))
            task.description?.let { description ->
                Text(
                    text = description.ifEmpty { "" },
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.White.copy(alpha = 0.9f),
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateChip(
                    dueDate = task.dueDate,
                    currentDate = currentDate,
                    isDelayed = isDelayed
                )
                ActionButtons(
                    taskStatus = taskStatus,
                    onComplete = { updateTaskStatusAndUI("complete") },
                    onRevert = { updateTaskStatusAndUI("to do") },
                    onShowComments = {
                        task.id?.let { taskId ->
                            coroutineScope.launch {
                                commentController.getComments(taskId) { result ->
                                    if (result is Result.Success) {
                                        comments = result.data
                                        println("result.data: ${result.data}")
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
        }
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
                if (currentUserId == comment.user?.id) {
                    editingComment = comment
                } else {
                    showPermissionErrorDialog = true
                }
            },
            onDeleteComment = { comment ->
                if (currentUserId == comment.user?.id) {
                    coroutineScope.launch {
                        commentController.deleteComment(comment.id) { result ->
                            if (result is Result.Success) {
                                comments = comments.filter { it.id != comment.id }
                            } else {
                                println("Error al eliminar comentario: $result")
                            }
                        }
                    }
                } else {
                    showPermissionErrorDialog = true
                }
            }
        )
    }

    if (showPermissionErrorDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionErrorDialog = false },
            title = { Text("Permiso denegado") },
            text = { Text("No tiene permisos para borrar/editar otros comentarios") },
            confirmButton = {
                Button(
                    onClick = { showPermissionErrorDialog = false }
                ) {
                    Text("OK")
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

@Composable
private fun StatusIndicatorChip(statusText: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.caption.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
private fun DateChip(dueDate: Long?, currentDate: LocalDate, isDelayed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = dueDate?.let {
                formatDate(it, currentDate)
            } ?: "Sin fecha",
            style = MaterialTheme.typography.caption.copy(
                color = if (isDelayed) Color(0xFFFFCDD2) else Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
        )
    }
}

@Composable
private fun ActionButtons(
    taskStatus: String,
    onComplete: () -> Unit,
    onRevert: () -> Unit,
    onShowComments: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(
            onClick = onShowComments,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "💬",
                fontSize = 20.sp
            )
        }

        AnimatedVisibility(visible = taskStatus == "to do") {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp)
            ) {
                Text("Marcar como completada", fontSize = 13.sp)
            }
        }

        AnimatedVisibility(visible = taskStatus == "complete") {
            OutlinedButton(
                onClick = onRevert,
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text("Marcar como pendiente", fontSize = 13.sp)
            }
        }
    }
}
