package util.functions.space

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.space.SpaceData
import model.space.SpaceFeatures
import model.space.StatusData


@Composable
fun CreateSpace(
    onCreate: (SpaceData) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    val defaultStatuses = listOf(
        StatusData(status = "to do", type = "open", orderindex = 0, color = "#87909e"),
        StatusData(status = "in progress", type = "custom", orderindex = 1, color = "#5f55ee"),
        StatusData(status = "complete", type = "closed", orderindex = 2, color = "#008844")
    )

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
                            multiple_assignees = false,
                            features = features,
                            statuses = defaultStatuses
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