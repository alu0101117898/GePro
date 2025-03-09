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
import view.admin.AdminSpacesView
import controller.UsernameController
import data.UserInfo
import view.observer.ObserverHome
import view.resource.ResourceSpacesView

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("roleSelection") }
    var userRole by remember { mutableStateOf<String?>(null) }
    var teamId by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf<UserInfo?>(null) }
    val space = rememberCoroutineScope()

    val usernameController = remember { UsernameController(space) }


    LaunchedEffect(Unit) {
        usernameController.getUserInfo { result ->
            if (result is Result.Success) {
                println("Username: ${result.data}")
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
                currentScreen = when (role) {
                    "admin" -> "adminHome"
                    "observer" -> "observerHome"
                    "resource" -> "resourceHome"
                    else -> "resourceHome"
                }
            }
        }
        "adminHome" -> {
            if (teamId == null) {
                Text("Cargando información del equipo...")
            } else {
                val spaceController = remember { SpaceController(space) }
                val listController = remember { ListController(space) }
                val taskController = remember { TaskController(space) }
                AdminSpacesView(
                    spaceController = spaceController,
                    listController = listController,
                    taskController = taskController,
                    teamId = teamId!!,
                    onBack = { currentScreen = "roleSelection" },
                    userName = username?.formattedUsername ?: "Usuario"

                )
            }
        }
        "resourceHome" -> {
            if (teamId == null) {
                Text("Cargando información del equipo...")
            } else {
                val spaceController = remember { SpaceController(space) }
                val listController = remember { ListController(space) }
                val taskController = remember { TaskController(space) }
                ResourceSpacesView(
                    teamId = teamId!!,
                    currentUserId = username!!.userDetails.id,
                    spaceController = spaceController,
                    listController = listController,
                    taskController = taskController,
                    onTaskStateChanged = {},
                    onBack = { currentScreen = "roleSelection" },
                    )
            }
        }
        "observerHome" -> {
            if (teamId == null) {
                Text("Cargando información del equipo...")
            } else {
                val spaceController = remember { SpaceController(space) }
                val listController = remember { ListController(space) }
                val taskController = remember { TaskController(space) }
                ObserverHome(
                    teamId = teamId!!,
                    spaceController = spaceController,
                    listController = listController,
                    taskController = taskController,
                    onBack = { currentScreen = "roleSelection" },

                )
            }
        }
    }
}
