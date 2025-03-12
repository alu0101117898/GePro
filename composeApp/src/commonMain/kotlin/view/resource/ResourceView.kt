package view.resource

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controller.TaskController
import model.task.Task
import util.functions.task.resource.ResourceTaskItem
import util.errorhandling.Result

@Composable
fun ResourceSpacesView(
    teamId: String,
    currentUserId: Int,
    onBack: () -> Unit,
    spaceController: controller.SpaceController,
    listController: controller.ListController,
    taskController: TaskController,
    onTaskStateChanged: (Task) -> Unit
) {
    var spaces by remember { mutableStateOf<List<model.space.Space>>(emptyList()) }
    var spaceTasksMap by remember { mutableStateOf<Map<String, List<Task>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(teamId) {
        spaceController.getSpaces(teamId) { result ->
            when (result) {
                is Result.Success -> {
                    spaces = result.data
                    val tempMap = mutableMapOf<String, List<Task>>()
                    if (spaces.isEmpty()) {
                        isLoading = false
                    } else {
                        spaces.forEach { space ->
                            listController.getLists(space.id) { listResult ->
                                if (listResult is Result.Success) {
                                    val lists = listResult.data
                                    val tasksForSpace = mutableListOf<Task>()
                                    if (lists.isEmpty()) {
                                        tempMap[space.id] = emptyList()
                                    } else {
                                        var processed = 0
                                        lists.forEach { list ->
                                            taskController.getTasks(list.id) { taskResult ->
                                                if (taskResult is Result.Success) {
                                                    val filtered = taskResult.data.filter { task ->
                                                        task.assignees?.any { it.id == currentUserId } == true
                                                    }
                                                    tasksForSpace.addAll(filtered)
                                                }
                                                processed++
                                                if (processed == lists.size) {
                                                    tempMap[space.id] = tasksForSpace
                                                    if (tempMap.size == spaces.size) {
                                                        spaceTasksMap = tempMap.toMap()
                                                        isLoading = false
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    errorMessage = "Error al cargar listas para el espacio ${space.name}"
                                    isLoading = false
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    errorMessage = "Error al cargar espacios: ${result.error}"
                    isLoading = false
                }
                else -> { }
            }
        }
    }

    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Tus espacios con tareas asignadas",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            } else {
                val filteredSpaces = spaces.filter { space ->
                    spaceTasksMap[space.id]?.isNotEmpty() == true
                }
                if (filteredSpaces.isEmpty()) {
                    Text("No tienes tareas asignadas en ningún espacio.")
                } else {
                    filteredSpaces.forEach { space ->
                        val tasksInSpace = spaceTasksMap[space.id] ?: emptyList()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = 4.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = space.name, style = MaterialTheme.typography.h6)
                                Text(
                                    text = "Este proyecto tiene ${tasksInSpace.size} tarea(s) asignadas.",
                                    style = MaterialTheme.typography.body2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                tasksInSpace.forEach { task ->
                                    ResourceTaskItem(
                                        task = task,
                                        currentUserId = currentUserId,
                                        taskController = taskController,
                                        onTaskStateChanged = onTaskStateChanged
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
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

