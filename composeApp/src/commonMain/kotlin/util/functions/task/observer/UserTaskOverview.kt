package util.functions.task.observer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.space.Space
import model.task.Task
import model.task.User

@Composable
fun UserTasksOverview(
    userTasks: Map<User?, List<Task>>,
    spaces: List<Space>,
    listSpaceMap: Map<String, String>,
    spaceTasksMap: Map<String, List<Task>>
) {
    Column {
        userTasks.forEach { (user, tasks) ->
            if (user != null) {
                val tasksBySpace = mutableMapOf<String, MutableList<Task>>()
                val spaceIdNameMap = mutableMapOf<String, String>()
                spaces.forEach { space ->
                    spaceIdNameMap[space.id] = space.name
                }

                tasks.forEach { task ->
                    val listId = task.list?.id
                    if (listId != null) {
                        val spaceId = listSpaceMap[listId]
                        if (spaceId != null) {
                            val spaceName = spaces.find { it.id == spaceId }?.name ?: "Espacio desconocido"
                            tasksBySpace.getOrPut(spaceName) { mutableListOf() }.add(task)
                            spaceIdNameMap[spaceId] = spaceName
                        } else {
                            tasksBySpace.getOrPut("Espacio desconocido") { mutableListOf() }.add(task)
                        }
                    } else {
                        tasksBySpace.getOrPut("Sin lista asignada") { mutableListOf() }.add(task)
                    }
                }
                println("Usuario: ${user.username}")
                tasksBySpace.forEach { (space, spaceTasks) ->
                    println("  - $space: ${spaceTasks.size} tareas")
                }

                UserTaskSection(
                    user = user,
                    tasksBySpace = tasksBySpace,
                    totalTasks = tasks.size,
                    spaceTasksMap = spaceTasksMap,
                    spaceIdNameMap = spaceIdNameMap,
                    tasks = tasks
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}