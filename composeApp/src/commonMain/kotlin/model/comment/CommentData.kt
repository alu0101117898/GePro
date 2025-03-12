package model.comment

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val comment: List<CommentPart> = emptyList(),
    val comment_text: String = "",
    val user: CommentUser? = null,
    val date: Long = 0L,
    val reply_count: Int = 0
)

@Serializable
data class CommentPart(
    val text: String,
    val attributes: Map<String, String> = emptyMap()
)

@Serializable
data class CommentUser(
    val id: Int,
    val username: String,
    val email: String,
    val color: String? = null,
    val initials: String,
    val profilePicture: String? = null
)

@Serializable
data class CommentsResponse(
    val comments: List<Comment>
)

@Serializable
data class CommentUpdateData(
    val comment_text: String,
    val assignee: Int? = null,
    val notify_all: Boolean = true
)
