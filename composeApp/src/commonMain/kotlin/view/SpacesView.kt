package view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import controller.SpaceController
import model.space.Space
import util.Result

@Composable
fun SpacesView(
    spaceController: SpaceController,
    teamId: String,
    onBack: () -> Unit
) {
    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(teamId) {
        spaceController.getSpaces(teamId) { result ->
            isLoading = false
            when(result) {
                is Result.Success -> spaces = result.data
                is Result.Error -> errorMessage = "Error al cargar espacios: ${result.error}"
                Result.Loading -> isLoading = true
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage)
        } else {
            spaces.forEach { space ->
                SpaceItem(space = space)
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
fun SpaceItem(
    space: Space,
    onEdit: () -> Unit = { /* Aquí se implementaría editar */ },
    onDelete: () -> Unit = { /* Aquí se implementaría borrar */ }
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { expanded = true },
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

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(onClick = {
            expanded = false
            onEdit()
        }) {
            Text("Editar")
        }
        DropdownMenuItem(onClick = {
            expanded = false
            onDelete()
        }) {
            Text("Borrar")
        }
    }
}