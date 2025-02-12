import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import networking.ClickUpClient
import org.jetbrains.compose. ui.tooling.preview.Preview
import util.Result
import util.PostSpaceData
import util.SpaceFeatures

@Composable
@Preview
fun App(client: ClickUpClient) {
    var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var multipleAssignees by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showForm = !showForm }) {
            Text("Add Space")
        }

        if (showForm) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = multipleAssignees,
                        onCheckedChange = { multipleAssignees = it }
                    )
                    Text("Multiple Assignees")
                }
                Button(onClick = {
                    coroutineScope.launch {
                        if (name.isBlank()) {
                            message = "Space name cannot be empty"
                            return@launch
                        }
                        isLoading = true
                        val spaceFeatures = SpaceFeatures(
                            due_dates = SpaceFeatures.DueDates(true, true, true, true),
                            time_tracking = SpaceFeatures.TimeTracking(true),
                            tags = SpaceFeatures.Tags(true),
                            time_estimates = SpaceFeatures.TimeEstimates(true),
                            checklists = SpaceFeatures.Checklists(true),
                            custom_fields = SpaceFeatures.CustomFields(true),
                            remap_dependencies = SpaceFeatures.RemapDependencies(true),
                            dependency_warning = SpaceFeatures.DependencyWarning(true),
                            portfolios = SpaceFeatures.Portfolios(true)
                        )
                        val postData = PostSpaceData(name, multipleAssignees, spaceFeatures)
                        val result = client.postClickUpSpace("9012384575", Json.encodeToString(postData))
                        isLoading = false
                        message = when (result) {
                            is Result.Success -> "Space created successfully!"
                            is Result.Error -> "Error creating space: ${result.error}"
                            Result.Loading -> "Loading..."
                        }
                    }
                }) {
                    Text("Submit")
                }
                if (isLoading) {
                    CircularProgressIndicator()
                }
                Text(message)
            }
        }
    }
}