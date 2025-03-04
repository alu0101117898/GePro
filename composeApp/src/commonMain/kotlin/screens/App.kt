package screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import controller.ListController
import controller.SpaceController
import controller.TaskController
import org.jetbrains.compose.ui.tooling.preview.Preview
import repository.TeamRepository
import util.errorhandling.Result
import view.SpacesView
import controller.UsernameController

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("roleSelection") }
    var userRole by remember { mutableStateOf<String?>(null) }
    var teamId by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf<String?>(null) }
    val space = rememberCoroutineScope()

    val usernameController = remember { UsernameController(space) }


    LaunchedEffect(Unit) {
        usernameController.getUsername { result ->
            if (result is Result.Success) {
                username = result.data
            }
        }
    }

    LaunchedEffect(Unit) {
        val result = TeamRepository.getTeams()
        if (result is Result.Success && result.data.isNotEmpty()) {
            teamId = result.data.first().id
        }
    }

    when (currentScreen) {
        "roleSelection" -> {
            RoleSelectionView { role ->
                userRole = role
                currentScreen = if (role == "admin") {
                    "spaces"
                } else {
                    "resourceHome"
                }
            }
        }
        "spaces" -> {
            if (teamId == null) {
                Text("Cargando información del equipo...")
            } else {
                val spaceController = remember { SpaceController(space) }
                val listController = remember { ListController(space) }
                val taskController = remember { TaskController(space) }
                SpacesView(
                    spaceController = spaceController,
                    listController = listController,
                    taskController = taskController,
                    teamId = teamId!!,
                    onBack = { currentScreen = "roleSelection" },
                    userName = username ?: "Usuario"

                )
            }
        }
        "resourceHome" -> {
            Text("Pantalla para recursos aún no implementada.")
        }
    }
}
