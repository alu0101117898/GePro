package util

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val id: String,
    val name: String,
    val description: String?,
    val creator: Creator?
)

@Serializable
data class Creator(
    val username: String
)