package util.functions.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import model.comment.Comment

@Composable
fun CommentDialog(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onCreateComment: (String) -> Unit,
    onEditComment: (Comment) -> Unit,
    onDeleteComment: (Comment) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 200.dp, max = 500.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(8.dp)
                ) {
                    Column {
                        if (comments.isEmpty()) {
                            Text(
                                "No hay comentarios",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .alpha(0.6f),
                                style = MaterialTheme.typography.body2
                            )
                        } else {
                            comments.forEach { comment ->
                                CommentItem(
                                    comment = comment,
                                    onEdit = onEditComment,
                                    onDelete = onDeleteComment
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Divider(color = Color.LightGray, thickness = 0.5.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (newCommentText.isNotBlank()) {
                                    onCreateComment(newCommentText)
                                    newCommentText = ""
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onCreateComment(newCommentText)
                                newCommentText = ""
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.maxValue)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar", color = MaterialTheme.colors.primary)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(0.9f),
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(12.dp)
    )
}




