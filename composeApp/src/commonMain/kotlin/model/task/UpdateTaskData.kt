package model.task

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UpdateTaskData(
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val assignees: AssigneesUpdate? = null,
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
data class CreateTaskData(
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val assignees: List<Int>? = null,
    val tags: List<String>? = null,
    @SerialName("due_date")
    val dueDate: Long,
    val priority: Int? = null,
    val notifyAll: Boolean = true,
    val parent: String? = null,
    val linksTo: String? = null,
    @SerialName("custom_fields")
    val customFields: List<CustomField>? = null
)

@Serializable
data class CustomField(
    val id: String,
    val value: String
)

@Serializable
data class AssigneesUpdate(
    val add: List<Int>? = null,
    val rem: List<Int>? = null
)

fun Task.toTaskData(): UpdateTaskData = UpdateTaskData(
    name = name,
    description = description,
    dueDate = dueDate!!,
    status = status?.status
)
