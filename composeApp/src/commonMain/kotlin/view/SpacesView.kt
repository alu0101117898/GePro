package view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import controller.SpaceController
import data.SpaceData
import data.SpaceFeatures
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
    var showCreateErrorDialog by remember { mutableStateOf(false) } // Para manejar error al superar 5 espacios
    var showCreateDialog by remember { mutableStateOf(false) } // Para mostrar el diálogo de creación

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
                        onEdit = { updatedSpace ->
                            onSpaceEdited(updatedSpace)
                        },
                        onDelete = { deletedSpace ->
                            onSpaceDeleted(deletedSpace)
                        }
                    )
                    Spacer(modifier = Modifier.padding(top = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
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
    
    if (showCreateDialog) {
        CreateSpace(
            teamId = teamId,
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

@Composable
fun CreateSpace(
    teamId: String,
    onCreate: (SpaceData) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nuevo Espacio") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Checkbox(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    Text(
                        text = if (isPrivate) "Privado" else "Público",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val features = SpaceFeatures(
                            due_dates = SpaceFeatures.DueDates(true, true, true, false),
                            time_tracking = SpaceFeatures.TimeTracking(true),
                            tags = SpaceFeatures.Tags(true),
                            time_estimates = SpaceFeatures.TimeEstimates(true),
                            checklists = SpaceFeatures.Checklists(true),
                            custom_fields = SpaceFeatures.CustomFields(true),
                            remap_dependencies = SpaceFeatures.RemapDependencies(true),
                            dependency_warning = SpaceFeatures.DependencyWarning(true),
                            portfolios = SpaceFeatures.Portfolios(true)
                        )
                        val spaceData = SpaceData(
                            name = name,
                            multiple_assignees = false, // Puedes ajustar este valor según la lógica
                            features = features
                        )
                        onCreate(spaceData)
                    }
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
