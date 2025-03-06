package model

import kotlinx.serialization.Serializable

/**
 * Represents a team.
 * @param id The team's ID.
 * @param name The team's name.
 */
@Serializable
data class Team(
    val id: String,
    val name: String
)