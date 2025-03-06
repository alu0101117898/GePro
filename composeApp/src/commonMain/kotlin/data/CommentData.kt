@file:Suppress("PropertyName")

package data

import kotlinx.serialization.Serializable

/**
 * Represents a comment in ClickUp.
 * @property id The ID of the comment.
 * @property comment The comment text, split into parts.
 * @property comment_text The comment text.
 * @property user The user who made the comment.
 * @property date The date the comment was made.
 * @property reply_count The number of replies to the comment.
 */
@Serializable
data class Comment(
    val id: String,
    val comment: List<CommentPart> = emptyList(),
    val comment_text: String = "",
    val user: CommentUser? = null,
    val date: Long = 0L,
    val reply_count: Int = 0
)

/**
 * Represents a part of a comment in ClickUp.
 * @property text The text of the comment part.
 * @property attributes Any attributes of the comment part.
 */
@Serializable
data class CommentPart(
    val text: String,
    val attributes: Map<String, String> = emptyMap()
)

/**
 * Represents a user who made a comment in ClickUp.
 * @property id The ID of the user.
 * @property username The username of the user.
 * @property email The email of the user.
 * @property color The color of the user.
 * @property initials The initials of the user.
 * @property profilePicture The profile picture of the user.
 */
@Serializable
data class CommentUser(
    val id: Int,
    val username: String,
    val email: String,
    val color: String? = null,
    val initials: String,
    val profilePicture: String? = null
)

/**
 * Represents a response containing comments in ClickUp.
 * @property comments The comments in the response.
 */
@Serializable
data class CommentsResponse(
    val comments: List<Comment>
)

/**
 * Represents data to update a comment in ClickUp.
 * @property comment_text The new comment text.
 * @property assignee The ID of the user to assign the comment to.
 * @property notify_all Whether to notify all assignees.
 */
@Serializable
data class CommentUpdateData(
    val comment_text: String,
    val assignee: Int? = null,
    val notify_all: Boolean = true
)
