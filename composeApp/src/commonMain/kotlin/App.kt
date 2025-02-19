import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.TaskController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Task
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Result
import view.TaskView

@Composable
@Preview
fun App() {
    val taskId = "8697z3p78"
    val taskController = TaskController(rememberCoroutineScope())
    var task by remember { mutableStateOf<Task?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        taskController.getTask(taskId) { result ->
            isLoading = false
            when (result) {
                is Result.Success -> task = result.data
                is Result.Error -> errorMessage = "Error loading task: ${result.error}"
                Result.Loading -> isLoading = true
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            task?.let { TaskView(task = it) } ?: Text(text = errorMessage)
        }
    }
}
