package model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String,
    val name: String,
    val color: String? = null,
    val avatar: String? = null,
    val members: List<TeamMember> = emptyList()
)

@Serializable
data class TeamMember(
    val user: User
)

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val color: String? = null,
    val initials: String,
    val profilePicture: String? = null
)
