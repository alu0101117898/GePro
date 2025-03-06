package model

import kotlinx.serialization.Serializable

/**
 * Represents a task list.
 * @property id The unique identifier of the task list.
 * @property name The name of the task list.
 */
@Serializable
data class TaskList(
    val id: String,
    val name: String,
)