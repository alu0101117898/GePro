package util.functions.task.observer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import model.task.User

@Composable
fun ContactDialog(
    user: User,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Información de contacto",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Email:",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp)
                        )

                        Text(
                            text = user.email ?: "No disponible",
                            style = MaterialTheme.typography.body1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}
