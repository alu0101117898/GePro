package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import controller.SpaceController
import kotlinx.coroutines.delay
import model.space.Space
import util.Result
import util.functions.spaces.CreateSpace
import util.functions.spaces.SpaceItem

@Composable
fun SpacesView(
    spaceController: SpaceController,
    teamId: String,
    onBack: () -> Unit,
    onSpaceEdited: (Space) -> Unit = {},
    onSpaceDeleted: (Space) -> Unit = {}
) {
    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var showCreateErrorDialog by remember { mutableStateOf(false) } // Límite de 5 espacios
    var showCreateDialog by remember { mutableStateOf(false) } // Mostrar diálogo de creación

    // Estados para los diálogos de edición y borrado
    var editingSpace by remember { mutableStateOf<Space?>(null) }
    var editedName by remember { mutableStateOf("") }
    var deletingSpace by remember { mutableStateOf<Space?>(null) }

    // Función para refrescar espacios
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                spaces.forEach { space ->
                    SpaceItem(
                        space = space,
                        onEdit = { selectedSpace ->
                            editingSpace = selectedSpace
                            editedName = selectedSpace.name
                        },
                        onDelete = { selectedSpace ->
                            deletingSpace = selectedSpace
                        }
                    )
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
        FloatingActionButton(
            onClick = {
                if (spaces.size >= 5) {
                    showCreateErrorDialog = true
                } else {
                    showCreateDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Crear Espacio")
        }
    }

    // Diálogo de error si se supera el límite gratuito de 5 espacios.
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

    // Diálogo para crear un nuevo espacio.
    if (showCreateDialog) {
        CreateSpace(
            onCreate = { newSpaceData ->
                spaceController.createSpace(teamId, newSpaceData) { result ->
                    when (result) {
                        is Result.Success -> {
                            refreshSpaces()
                        }
                        is Result.Error -> {
                            errorMessage = "Error al crear espacio: ${result.error}"
                        }
                        else -> { }
                    }
                }
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    // Diálogo para confirmar la eliminación de un espacio.
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

    // Diálogo para editar un espacio: muestra un campo de texto para modificar el nombre.
    if (editingSpace != null) {
        AlertDialog(
            onDismissRequest = { editingSpace = null },
            title = { Text("Editar espacio") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nuevo nombre") }
                    )
                }
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
}
