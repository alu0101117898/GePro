package util.functions.comment

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import model.comment.Comment

@Composable
fun EditCommentDialog(
    comment: Comment,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var commentText by remember { mutableStateOf(comment.comment_text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Comentario") },
        text = {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(commentText) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
