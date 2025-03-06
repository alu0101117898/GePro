package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controller.SpaceController
import kotlinx.coroutines.delay
import model.task.Task
import model.space.Space
import screens.AdminHeader
import util.errorhandling.Result
import util.functions.space.CreateSpace
import util.functions.space.SpaceItem
import controller.ListController
import controller.TaskController
import util.errorhandling.NetworkError

@Composable
fun SpacesView(
    spaceController: SpaceController,
    taskController: TaskController,
    listController: ListController,
    teamId: String,
    userName: String,
    onBack: () -> Unit,
    onSpaceEdited: (Space) -> Unit = {},
    onSpaceDeleted: (Space) -> Unit = {}
) {
    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showCreateErrorDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    var listId by remember { mutableStateOf<String?>(null) }
    var editingSpace by remember { mutableStateOf<Space?>(null) }
    var editedName by remember { mutableStateOf("") }
    var deletingSpace by remember { mutableStateOf<Space?>(null) }

    fun refreshSpaces() {
        spaceController.getSpaces(teamId) { result ->
            when (result) {
                is Result.Success -> {
                    spaces = result.data
                    errorMessage = ""
                }
                is Result.Error -> errorMessage = "Error al cargar espacios: ${result.error}"
                Result.Loading -> { }
            }
            isLoading = false
        }
    }

    LaunchedEffect(teamId) {
        refreshSpaces()
        while (true) {
            delay(30000L)
            refreshSpaces()
        }
    }

    val onLoadTasks: (Space, (Result<List<Task>, NetworkError>) -> Unit) -> Unit = { space, onResult ->
        listController.getLists(space.id) { listResult ->
            when (listResult) {
                is Result.Success -> {
                    val lists = listResult.data
                    val tasks = mutableListOf<Task>()
                    var processed = 0
                    if (lists.isEmpty()) {
                        onResult(Result.Success(emptyList()))
                    }
                    listId = lists.first().id
                    lists.forEach { taskList ->
                        taskController.getTasks(taskList.id) { taskResult ->
                            if (taskResult is Result.Success) {
                                tasks.addAll(taskResult.data)
                            }
                            processed++
                            if (processed == lists.size) {
                                onResult(Result.Success(tasks))
                            }
                        }
                    }
                }
                is Result.Error -> {
                    onResult(Result.Error(listResult.error))
                }
                Result.Loading -> { }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AdminHeader(
                userName = userName,
                onCreateProject = {
                    if (spaces.size >= 5) {
                        showCreateErrorDialog = true
                    } else {
                        showCreateDialog = true
                    }
                },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, modifier = Modifier.padding(16.dp))
                } else {
                    spaces.forEach { space ->
                        SpaceItem(
                            space = space,
                            taskController = taskController,
                            onEdit = { selectedSpace ->
                                editingSpace = selectedSpace
                                editedName = selectedSpace.name
                            },
                            onDelete = { selectedSpace ->
                                deletingSpace = selectedSpace
                            },
                            onLoadTasks = onLoadTasks,
                            listId = listId.toString(),
                        )
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
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

    if (showCreateDialog) {
        CreateSpace(
            onCreate = { newSpaceData ->
                spaceController.createSpace(teamId, newSpaceData) { result ->
                    when (result) {
                        is Result.Success -> {
                            val newSpace = result.data
                            listController.createList(newSpace.id, "List") { listResult ->
                                when (listResult) {
                                    is Result.Success -> refreshSpaces()
                                    is Result.Error -> {
                                        errorMessage = "Error al crear lista: ${listResult.error}"
                                    }
                                    else -> {}
                                }
                            }
                        }
                        is Result.Error -> { errorMessage = "Error al crear espacio: ${result.error}" }
                        else -> {}
                    }
                }
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    if (deletingSpace != null) {
        AlertDialog(
            onDismissRequest = { deletingSpace = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de que desea eliminar el espacio \"${deletingSpace!!.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    deletingSpace?.let { spaceToDelete ->
                        spaceController.deleteSpace(spaceToDelete.id) { result ->
                            when (result) {
                                is Result.Success -> {
                                    onSpaceDeleted(spaceToDelete)
                                    refreshSpaces()
                                }
                                is Result.Error -> {
                                    errorMessage = "Error al eliminar el espacio: ${result.error}"
                                }
                                else -> {}
                            }
                        }
                    }
                    deletingSpace = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSpace = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (editingSpace != null) {
        AlertDialog(
            onDismissRequest = { editingSpace = null },
            title = { Text("Editar espacio") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nuevo nombre") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    editingSpace?.let { spaceToEdit ->
                        val updatedSpace = spaceToEdit.copy(name = editedName)
                        spaceController.updateSpace(updatedSpace) { result ->
                            when (result) {
                                is Result.Success -> {
                                    onSpaceEdited(result.data)
                                    refreshSpaces()
                                }
                                is Result.Error -> {
                                    errorMessage = "Error al actualizar el espacio: ${result.error}"
                                }
                                else -> {}
                            }
                        }
                    }
                    editingSpace = null
                }) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSpace = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showCreateErrorDialog) {
        AlertDialog(
            onDismissRequest = { showCreateErrorDialog = false },
            title = { Text("Límite alcanzado") },
            text = { Text("El plan gratuito solo permite hasta 5 espacios.") },
            confirmButton = {
                TextButton(onClick = { showCreateErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
