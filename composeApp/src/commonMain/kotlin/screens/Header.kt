package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminHeader(
    userName: String,
    onCreateProject: () -> Unit = {},
    onViewProjectStatus: () -> Unit = {},
    onDelayedTasks: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var buttonPosition by remember { mutableStateOf(DpOffset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Hola, $userName",
            fontSize = 28.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInWindow()
                    buttonPosition = DpOffset(position.x.dp, position.y.dp)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú de opciones",
                tint = Color.Black
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            offset = buttonPosition
        ) {
            DropdownMenuItem(onClick = {
                menuExpanded = false
                onCreateProject()
            }) {
                Text("Crear proyecto")
            }
            DropdownMenuItem(onClick = {
                menuExpanded = false
                onViewProjectStatus()
            }) {
                Text("Ver estado del proyecto")
            }
            DropdownMenuItem(onClick = {
                menuExpanded = false
                onDelayedTasks()
            }) {
                Text("Tareas retrasadas")
            }
        }
    }
}
