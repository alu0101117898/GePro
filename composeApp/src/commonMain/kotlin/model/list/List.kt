package model.list

import kotlinx.serialization.Serializable

@Serializable
data class TaskList(
    val id: String,
    val name: String,
)