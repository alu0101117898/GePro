package data

import kotlinx.serialization.Serializable
import model.task.Task
import kotlinx.serialization.SerialName


/**
 * Data class for task data
 * @property name name of the task
 * @property description description of the task
 * @property status status of the task
 * @property assignees list of assignees
 * @property tags list of tags
 * @property dueDate due date of the task
 * @property startDate start date of the task
 * @property priority priority of the task
 * @property notifyAll notify all assignees
 * @property parent parent task
 * @property linksTo links to
 * @property checkRequiredCustomFields check required custom fields
 * @property customFields list of custom fields
 * @constructor Create empty Task data
 */
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

/**
 * Data class for custom field
 * @property id id of the custom field
 * @property value value of the custom field
 * @constructor Create empty Custom field
 */
@Serializable
data class CustomField(
    val id: String,
    val value: String
)

/**
 * Convert task to task data
 * @receiver Task
 * @return Task data
 */
fun Task.toTaskData(): TaskData = TaskData(
    name = name,
    description = description,
    dueDate = dueDate!!,
    status = status?.status
)