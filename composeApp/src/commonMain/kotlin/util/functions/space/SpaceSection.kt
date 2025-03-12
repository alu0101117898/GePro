package util.functions.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.task.Task
import util.functions.task.observer.ObserverTaskItem


@Composable
fun SpaceSection(spaceName: String, tasks: List<Task>, allSpaceTasks: List<Task>) {
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = spaceName,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { showStatusDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Estado")
                }
            }

            tasks.forEach { task ->
                ObserverTaskItem(task = task)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    if (showStatusDialog) {
        SpaceStatusDialog(
            tasks = allSpaceTasks,
            onDismiss = { showStatusDialog = false },
            spaceName = spaceName
        )
    }
}