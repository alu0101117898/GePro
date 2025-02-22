package data

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val assignees: List<Long>? = null,
    val tags: List<String>? = null,
    val dueDate: Long? = null,
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