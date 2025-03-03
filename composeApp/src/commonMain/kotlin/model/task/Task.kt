package model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String? = null,
    @SerialName("custom_id") val customId: String? = null,
    @SerialName("custom_item_id") val customItemId: Int? = null,
    val name: String,
    @SerialName("text_content") val textContent: String? = null,
    val description: String? = null,
    val status: Status? = null,
    @SerialName("orderindex") val orderIndex: String? = null,
    @SerialName("date_created") val dateCreated: Long? = null,
    @SerialName("date_updated") val dateUpdated: Long? = null,
    @SerialName("date_closed") val dateClosed: Long? = null,
    @SerialName("date_done") val dateDone: Long? = null,
    val archived: Boolean? = null,
    val creator: User? = null,
    val assignees: List<User>? = null,
    @SerialName("group_assignees") val groupAssignees: List<GroupAssignee>? = null,
    val watchers: List<User>? = null,
    val checklists: List<Checklist>? = null,
    val tags: List<Tag>? = null,
    val parent: String? = null,
    @SerialName("top_level_parent") val topLevelParent: String? = null,
    val priority: Priority? = null,
    @SerialName("due_date") val dueDate: Long? = null,
    @SerialName("start_date") val startDate: Long? = null,
    val points: Int? = null,
    @SerialName("time_estimate") val timeEstimate: Long? = null,
    @SerialName("time_spent") val timeSpent: Long? = null,
    @SerialName("custom_fields") val customFields: List<CustomField>? = null,
    val dependencies: List<Dependency>? = null,
    @SerialName("linked_tasks") val linkedTasks: List<LinkedTask>? = null,
    val locations: List<Location>? = null,
    @SerialName("team_id") val teamId: String? = null,
    val url: String? = null,
    val sharing: Sharing? = null,
    @SerialName("permission_level") val permissionLevel: String? = null,
    val list: ListInfo? = null,
    val project: ProjectInfo? = null,
    val folder: FolderInfo? = null,
    val space: SpaceInfo? = null
)

@Serializable
data class Status(
    val id: String? = null,
    val status: String,
    val color: String? = null,
    val orderindex: Int? = null,
    val type: String? = null
)

@Serializable
data class User(
    val id: Long,
    val username: String,
    val color: String? = null,
    val initials: String? = null,
    val email: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null
)

@Serializable
data class GroupAssignee(
    val id: String? = null,
    val name: String? = null,
    val color: String? = null,
    val initials: String? = null,
    val email: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null
)

@Serializable
data class Checklist(
    val id: String? = null,
    val name: String? = null,
    val orderindex: Int? = null,
    val tasks: List<Task>? = null
)

@Serializable
data class Tag(
    val id: String? = null,
    val name: String? = null,
    val color: String? = null,
    val orderindex: Int? = null
)

@Serializable
data class Priority(
    val id: String? = null,
    val priority: String? = null,
    val color: String? = null,
    val orderindex: String? = null
)

@Serializable
data class CustomField(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val value: String? = null,
    val options: List<String>? = null,
    val orderindex: Int? = null
)

@Serializable
data class Dependency(
    val id: String? = null,
    val name: String? = null,
    val status: String? = null,
    val orderindex: Int? = null
)

@Serializable
data class LinkedTask(
    val id: String? = null,
    val name: String? = null,
    val status: String? = null,
    val orderindex: Int? = null
)

@Serializable
data class Location(
    val id: String? = null,
    val name: String? = null,
    val orderindex: Int? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class Sharing(
    val public: Boolean? = null,
    @SerialName("public_share_expires_on") val publicShareExpiresOn: String? = null,
    @SerialName("public_fields") val publicFields: List<String>? = null,
    val token: String? = null,
    @SerialName("seo_optimized") val seoOptimized: Boolean? = null
)

@Serializable
data class ListInfo(
    val id: String? = null,
    val name: String? = null,
    val access: Boolean? = null
)

@Serializable
data class ProjectInfo(
    val id: String? = null,
    val name: String? = null,
    val hidden: Boolean? = null,
    val access: Boolean? = null
)

@Serializable
data class FolderInfo(
    val id: String? = null,
    val name: String? = null,
    val hidden: Boolean? = null,
    val access: Boolean? = null
)

@Serializable
data class SpaceInfo(
    val id: String? = null
)
