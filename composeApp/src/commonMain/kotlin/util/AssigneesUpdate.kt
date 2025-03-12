package util

import model.task.AssigneesUpdate

fun createAssigneesUpdate(currentAssignee: model.task.User?, newAssignee: model.task.User?): AssigneesUpdate? {
    val currentAssigneeId = currentAssignee?.id
    val newAssigneeId = newAssignee?.id

    return if (currentAssigneeId != newAssigneeId) {
        AssigneesUpdate(
            add = newAssigneeId?.let { listOf(it) },
            rem = currentAssigneeId?.let { listOf(it) }
        )
    } else {
        null
    }
}