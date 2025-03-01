package util.functions.spaces

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import model.space.Space

@Composable
fun SpaceItem(
    space: Space,
    onEdit: (Space) -> Unit = { },
    onDelete: (Space) -> Unit = { }
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