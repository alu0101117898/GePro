import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.TaskController
import model.Task
import networking.TaskFunction
import networking.createHttpClient
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Result
import view.CreateTaskView
import view.TaskView

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val httpClient = remember { createHttpClient(io.ktor.client.engine.cio.CIO.create()) }
    val taskFunction = remember { TaskFunction(httpClient) }
    val taskController = remember { TaskController(coroutineScope) }

    var currentScreen by remember { mutableStateOf("home") }
    var taskIdInput by remember { mutableStateOf("") }

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onViewTask = { currentScreen = "viewTask" },
                onCreateTask = { currentScreen = "createTask" },
                taskIdInput = taskIdInput,
                onTaskIdChange = { taskIdInput = it }
            )
        }
        "viewTask" -> {
            ViewTaskScreen(
                taskController = taskController,
                taskId = taskIdInput,
                onBack = { currentScreen = "home" }
            )
        }
        "createTask" -> {
            CreateTaskView(
                taskController = taskController
            )
            Button(onClick = { currentScreen = "home" }) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun HomeScreen(
    onViewTask: () -> Unit,
    onCreateTask: () -> Unit,
    taskIdInput: String,
    onTaskIdChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = taskIdInput,
            onValueChange = onTaskIdChange,
            label = { Text("ID de Tarea") }
        )
        Button(onClick = onViewTask) {
            Text("Ver Tarea")
        }
        Button(onClick = onCreateTask) {
            Text("Crear Tarea")
        }
    }
}

@Composable
fun ViewTaskScreen(
    taskController: TaskController,
    taskId: String,
    onBack: () -> Unit
) {
    var task by remember { mutableStateOf<Task?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        taskController.getTask(taskId) { result ->
            isLoading = false
            when (result) {
                is Result.Success -> task = result.data
                is Result.Error -> errorMessage = "Error al cargar la tarea: ${result.error}"
                Result.Loading -> isLoading = true
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            task?.let {
                TaskView(task = it)
            } ?: Text(text = errorMessage)
        }
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
