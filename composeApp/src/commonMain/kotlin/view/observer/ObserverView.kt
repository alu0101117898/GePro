package view.observer

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import util.errorhandling.Result
import util.functions.task.observer.ObserverViewContent

@Composable
fun ObserverView(
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
    val spaceTasksMap = remember { mutableStateOf<Map<String, List<Task>>>(emptyMap()) }

    LaunchedEffect(teamId) {
        spaceController.getSpaces(teamId) { spaceResult ->
            when (spaceResult) {
                is Result.Success -> {
                    spaces = spaceResult.data
                    val tempListSpaceMap = mutableMapOf<String, String>()
                    val tempAllTasks = mutableListOf<Task>()
                    val tempSpaceTasksMap = mutableMapOf<String, MutableList<Task>>()
                    var pendingLists = 0
                    var processedLists = 0

                    spaces.forEach { space ->
                        tempSpaceTasksMap[space.id] = mutableListOf()
                    }
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
                                                    val spaceId = space.id
                                                    tempSpaceTasksMap[spaceId]?.addAll(taskResult.data)
                                                    processedLists++
                                                    if (processedLists == pendingLists) {
                                                        allTasks = tempAllTasks
                                                        listSpaceMap.value = tempListSpaceMap
                                                        spaceTasksMap.value = tempSpaceTasksMap
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
            spaceTasksMap = spaceTasksMap.value
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

fun formatDueDate(dueDate: LocalDate, currentDate: LocalDate): String {
    return when {
        dueDate == currentDate -> "Hoy"
        dueDate < currentDate -> "Retraso: ${currentDate.daysUntil(dueDate)} días"
        else -> "${dueDate.dayOfMonth}/${dueDate.monthNumber}"
    }
}

fun List<Task>.sortedByStatus(): List<Task> {
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