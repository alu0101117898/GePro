package view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import controller.SpaceController
import kotlinx.coroutines.delay
import model.space.Space
import util.Result

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

    // Estados para manejar los diálogos de edición y eliminación
    var editingSpace by remember { mutableStateOf<Space?>(null) }
    var editedName by remember { mutableStateOf("") }
    var deletingSpace by remember { mutableStateOf<Space?>(null) }

    fun refreshSpaces() {
        spaceController.getSpaces(teamId) { result ->
            when(result) {
                is Result.Success -> spaces = result.data
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

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage)
        } else {
            spaces.forEach { space ->
                SpaceItem(
                    space = space,
                    onEdit = { selectedSpace ->
                        // Al pulsar "Editar", establecemos el espacio a editar y su nombre actual
                        editingSpace = selectedSpace
                        editedName = selectedSpace.name
                    },
                    onDelete = { selectedSpace ->
                        // Al pulsar "Borrar", guardamos el espacio a eliminar.
                        deletingSpace = selectedSpace
                    }
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }

    // AlertDialog para confirmar eliminación del espacio.
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

    // AlertDialog para editar el nombre del espacio.
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
}

@Composable
fun SpaceItem(
    space: Space,
    onEdit: (Space) -> Unit = { /* Aquí se implementaría editar */ },
    onDelete: (Space) -> Unit = { /* Aquí se implementaría borrar */ }
) {
    var expanded by remember { mutableStateOf(false) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        tapOffset = offset
                        expanded = true
                    }
                )
            },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = space.name,
                style = MaterialTheme.typography.h6,
                color = Color.Black
            )
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = "ID: ${space.id}",
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray
            )
        }
    }

    val offsetDp = DpOffset(
        density.run { tapOffset.x.toDp() },
        density.run { tapOffset.y.toDp() }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = offsetDp
    ) {
        DropdownMenuItem(onClick = {
            expanded = false
            onEdit(space)
        }) {
            Text("Editar")
        }
        DropdownMenuItem(onClick = {
            expanded = false
            onDelete(space)
        }) {
            Text("Borrar")
        }
    }
}