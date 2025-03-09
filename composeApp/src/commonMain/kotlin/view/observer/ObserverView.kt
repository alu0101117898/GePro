package view.observer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import controller.ListController
import controller.SpaceController
import controller.TaskController
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import model.space.Space
import model.task.Task
import model.task.User
import util.errorhandling.Result
import util.functions.UserStatusDialog
import util.parseColor

@Composable
fun ObserverHome(
    teamId: String,
    onBack: () -> Unit,
    spaceController: SpaceController,
    listController: ListController,
    taskController: TaskController
) {
    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var allTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val listSpaceMap = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(teamId) {
        spaceController.getSpaces(teamId) { spaceResult ->
            when (spaceResult) {
                is Result.Success -> {
                    spaces = spaceResult.data
                    val tempListSpaceMap = mutableMapOf<String, String>()
                    val tempAllTasks = mutableListOf<Task>()
                    var pendingLists = 0
                    var processedLists = 0

                    spaces.forEach { space ->
                        listController.getLists(space.id) { listResult ->
                            when (listResult) {
                                is Result.Success -> {
                                    pendingLists += listResult.data.size
                                    listResult.data.forEach { list ->
                                        tempListSpaceMap[list.id] = space.id
                                        taskController.getTasks(list.id) { taskResult ->
                                            when (taskResult) {
                                                is Result.Success -> {
                                                    tempAllTasks.addAll(taskResult.data)
                                                    processedLists++

                                                    if (processedLists == pendingLists) {
                                                        allTasks = tempAllTasks
                                                        listSpaceMap.value = tempListSpaceMap
                                                        isLoading = false
                                                    }
                                                }
                                                is Result.Error -> {
                                                    errorMessage = "Error cargando tareas: ${taskResult.error}"
                                                    processedLists++

                                                    if (processedLists == pendingLists) {
                                                        isLoading = false
                                                    }
                                                }
                                                is Result.Loading -> { /* En proceso */ }
                                            }
                                        }
                                    }
                                }
                                is Result.Error -> {
                                    errorMessage = "Error cargando listas: ${listResult.error}"
                                    isLoading = false
                                }
                                is Result.Loading -> { /* En proceso */ }
                            }
                        }
                    }

                    // Si no hay espacios, terminamos la carga
                    if (spaces.isEmpty()) {
                        isLoading = false
                    }
                }
                is Result.Error -> {
                    errorMessage = "Error cargando espacios: ${spaceResult.error}"
                    isLoading = false
                }
                is Result.Loading -> { /* En proceso */ }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ObserverViewContent(
            isLoading = isLoading,
            errorMessage = errorMessage,
            allTasks = allTasks,
            spaces = spaces,
            listSpaceMap = listSpaceMap.value,
        )
        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .height(40.dp)
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun ObserverViewContent(
    isLoading: Boolean,
    errorMessage: String,
    allTasks: List<Task>,
    spaces: List<Space>,
    listSpaceMap: Map<String, String>,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Vista de Observador", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> CircularProgressIndicator()
            errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red)
            else -> {
                println("Total de tareas cargadas: ${allTasks.size}")

                val userTasks = allTasks
                    .filter { it.assignees?.isNotEmpty() == true }
                    .groupBy { it.assignees?.first() }

                println("Usuarios con tareas: ${userTasks.keys.size}")

                UserTasksOverview(userTasks, spaces, listSpaceMap)
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun UserTasksOverview(
    userTasks: Map<User?, List<Task>>,
    spaces: List<Space>,
    listSpaceMap: Map<String, String>
) {
    Column {
        userTasks.forEach { (user, tasks) ->
            if (user != null) {
                val tasksBySpace = mutableMapOf<String, MutableList<Task>>()
                tasks.forEach { task ->
                    val listId = task.list?.id
                    if (listId != null) {
                        val spaceId = listSpaceMap[listId]
                        if (spaceId != null) {
                            val spaceName = spaces.find { it.id == spaceId }?.name ?: "Espacio desconocido"
                            tasksBySpace.getOrPut(spaceName) { mutableListOf() }.add(task)
                        } else {
                            tasksBySpace.getOrPut("Espacio desconocido") { mutableListOf() }.add(task)
                        }
                    } else {
                        tasksBySpace.getOrPut("Sin lista asignada") { mutableListOf() }.add(task)
                    }
                }

                // Imprimir información de depuración
                println("Usuario: ${user.username}")
                tasksBySpace.forEach { (space, spaceTasks) ->
                    println("  - $space: ${spaceTasks.size} tareas")
                }

                UserTaskSection(
                    user = user,
                    tasksBySpace = tasksBySpace,
                    totalTasks = tasks.size,
                    tasks = tasks
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UserTaskSection(
    user: User,
    tasksBySpace: Map<String, List<Task>>,
    totalTasks: Int,
    tasks: List<Task>
) {
    var expanded by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.h5,
                    color = parseColor(user.color.toString()),
                    modifier = Modifier.clickable { expanded = !expanded }
                )

                Row {
                    Button(
                        onClick = { showStatusDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Estado")
                    }

                    Button(
                        onClick = { showContactDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Contacto")
                    }
                }
            }

            Text(
                text = "Tiene $totalTasks tareas asignada(s)",
                style = MaterialTheme.typography.body1,
                color = parseColor(user.color.toString())
            )

            AnimatedVisibility(visible = expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    tasksBySpace.forEach { (spaceName, spaceTasks) ->
                        if (spaceTasks.isNotEmpty()) {
                            SpaceSection(
                                spaceName = spaceName,
                                tasks = spaceTasks.sortedByStatus()
                            )
                        }
                    }
                }
            }
        }
    }

    if (showContactDialog) {
        ContactDialog(
            user = user,
            onDismiss = { showContactDialog = false }
        )
    }

    if (showStatusDialog) {
        UserStatusDialog(
            tasks = tasks,
            onDismiss = { showStatusDialog = false },
            userName = user.username
        )
    }
}

@Composable
private fun ContactDialog(
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

@Composable
private fun ObserverTaskItem(task: Task) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueDate = task.dueDate?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val (statusText, statusColor) = when {
        task.status?.status == "complete" -> Pair("COMPLETADA", MaterialTheme.colors.primary.copy(alpha = 0.9f))
        dueDate == null -> Pair("PENDIENTE", MaterialTheme.colors.secondary.copy(alpha = 0.9f))
        dueDate < currentDate -> Pair("RETRASADA", Color(0xFFE57373))
        dueDate == currentDate -> Pair("ÚLTIMO DÍA", Color(0xFFFFD54F))
        else -> Pair("PENDIENTE", MaterialTheme.colors.secondary.copy(alpha = 0.9f))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = statusColor
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.caption.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            dueDate?.let {
                Text(
                    text = formatDueDate(it, currentDate),
                    style = MaterialTheme.typography.caption.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

private fun formatDueDate(dueDate: LocalDate, currentDate: LocalDate): String {
    return when {
        dueDate == currentDate -> "Hoy"
        dueDate < currentDate -> "Retraso: ${currentDate.daysUntil(dueDate)} días"
        else -> "${dueDate.dayOfMonth}/${dueDate.monthNumber}"
    }
}

@Composable
private fun SpaceSection(spaceName: String, tasks: List<Task>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = spaceName,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            tasks.forEach { task ->
                ObserverTaskItem(task = task)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

private fun List<Task>.sortedByStatus(): List<Task> {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return sortedWith(compareBy { task ->
        when {
            task.status?.status == "complete" -> 4
            task.dueDate == null -> 3
            else -> {
                val dueDate = Instant.fromEpochMilliseconds(task.dueDate)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                when {
                    dueDate < currentDate -> 0
                    dueDate == currentDate -> 1
                    else -> 2
                }
            }
        }
    })
}