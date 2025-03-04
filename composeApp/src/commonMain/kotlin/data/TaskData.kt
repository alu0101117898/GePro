package data

import kotlinx.serialization.Serializable
import model.task.Task
import kotlinx.serialization.SerialName

@Serializable
data class TaskData(
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val assignees: List<Long>? = null,
    val tags: List<String>? = null,
    @SerialName("due_date")
    val dueDate: Long,
    val startDate: Long? = null,
    val priority: Long? = null,
    val notifyAll: Boolean = true,
    val parent: String? = null,
    val linksTo: String? = null,
    val checkRequiredCustomFields: Boolean = false,
    val customFields: List<CustomField>? = null
)

@Serializable
data class CustomField(
    val id: String,
    val value: String
)

fun Task.toTaskData() = TaskData(
    name = name,
    description = description,
    dueDate = dueDate!!,
    status = status?.status
)