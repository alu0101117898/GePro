package screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import repository.TeamRepository
import util.errorhandling.Result
import view.SpacesView

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("roleSelection") }
    var userRole by remember { mutableStateOf<String?>(null) }
    var teamId by remember { mutableStateOf<String?>(null) }
    val space = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val result = TeamRepository.getTeams()
        if (result is Result.Success && result.data.isNotEmpty()) {
            teamId = result.data.first().id
        }
    }

    when (currentScreen) {
        "roleSelection" -> {
            RoleSelectionView { role ->
                userRole = role
                currentScreen = if (role == "admin") {
                    "spaces"
                } else {
                    "resourceHome"
                }
            }
        }
        "spaces" -> {
            if (teamId == null) {
                Text("Cargando información del equipo...")
            } else {
                val spaceController = remember { controller.SpaceController(space) }
                val listController = remember { controller.ListController(space) }
                val taskController = remember { controller.TaskController(space) }
                SpacesView(
                    spaceController = spaceController,
                    listController = listController,
                    taskController = taskController,
                    teamId = teamId!!,
                    onBack = { currentScreen = "roleSelection" },
                    userName = userRole!!

                )
            }
        }
        "resourceHome" -> {
            Text("Pantalla para recursos aún no implementada.")
        }
    }
}
/*
@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val taskController = remember { TaskController(coroutineScope) }

    var currentScreen by remember { mutableStateOf("home") }
    var taskIdInput by remember { mutableStateOf("") }
    var task by remember { mutableStateOf<Task?>(null) }
    var confirmationMessage by remember { mutableStateOf("") } // Añadimos esta variable

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onViewTask = { currentScreen = "viewTask" },
                onCreateTask = { currentScreen = "createTask" },
                taskIdInput = taskIdInput,
                onTaskIdChange = { taskIdInput = it },
                confirmationMessage = confirmationMessage // Pasamos el mensaje a HomeScreen
            )
            // Limpiamos el mensaje después de mostrarlo
            LaunchedEffect(Unit) {
                confirmationMessage = ""
            }
        }
        "viewTask" -> {
            ViewTaskScreen(
                taskController = taskController,
                taskId = taskIdInput,
                onBack = { currentScreen = "home" },
                onEdit = { currentScreen = "updateTask" },
                onTaskLoaded = { loadedTask ->
                    task = loadedTask // Actualizamos `task` en `App()`
                },
                onDelete = {
                    task = null // Limpiamos la tarea
                    confirmationMessage = "¡Tarea eliminada exitosamente!" // Establecemos el mensaje de confirmación
                    currentScreen = "home" // Navegamos a la pantalla inicial
                }
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
        "updateTask" -> {
            task?.let { taskToEdit ->
                UpdateTaskView(
                    taskController = taskController,
                    task = taskToEdit,
                    onBack = { currentScreen = "viewTask" }
                )
            } ?: run {
                Text("No se encontró la tarea para editar.")
            }
        }
    }
}

@Composable
fun HomeScreen(
    onViewTask: () -> Unit,
    onCreateTask: () -> Unit,
    taskIdInput: String,
    onTaskIdChange: (String) -> Unit,
    confirmationMessage: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (confirmationMessage.isNotEmpty()) {
            Text(text = confirmationMessage)
        }
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
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onTaskLoaded: (Task) -> Unit,
    onDelete: () -> Unit // Este parámetro ya lo hemos añadido
) {
    var task by remember { mutableStateOf<Task?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) } // Para manejar el diálogo de confirmación

    LaunchedEffect(taskId) {
        taskController.getTask(taskId) { result ->
            isLoading = false
            when (result) {
                is Result.Success -> {
                    task = result.data
                    onTaskLoaded(result.data)
                }
                is Result.Error -> errorMessage = "Error al cargar la tarea: ${result.error}"
                Result.Loading -> isLoading = true
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de que desea eliminar esta tarea?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        taskController.deleteTask(taskId) { result ->
                            when (result) {
                                is Result.Success -> {
                                    onDelete()
                                }
                                is Result.Error -> {
                                    errorMessage = "Error al eliminar la tarea: ${result.error}"
                                }
                                else -> {}
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            task?.let {
                TaskView(task = it)
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Button(onClick = onEdit) {
                    Text("Editar Tarea")
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Button(onClick = {
                    showDialog = true // Mostrar el diálogo de confirmación
                }) {
                    Text("Eliminar Tarea")
                }
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage)
                }
            } ?: Text(text = errorMessage)
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
*/