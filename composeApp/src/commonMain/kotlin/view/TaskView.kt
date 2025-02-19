package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Task

@Composable
fun TaskView(task: Task, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Task ID: ${task.id}")
        Text(text = "Task Name: ${task.name}")
        Text(text = "Task Status: ${task.status}")
    }
}
