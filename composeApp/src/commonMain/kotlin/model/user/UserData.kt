package model.user

import kotlinx.serialization.Serializable

data class UserInfo(
    val formattedUsername: String,
    val userDetails: UserDetails
)

@Serializable
data class UserDetails(
    val id: Int,
    val username: String,
    val email: String,
    val color: String? = null,
    val profilePicture: String? = null,
    val initials: String
)
