package model.task

import data.CustomField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a task.
 * @param id The task ID.
 * @param customId The custom task ID.
 * @param customItemId The custom item ID.
 * @param name The task name.
 * @param textContent The task text content.
 * @param description The task description.
 * @param status The task status.
 * @param orderIndex The task order index.
 * @param dateCreated The task creation date.
 * @param dateUpdated The task update date.
 * @param dateClosed The task close date.
 * @param dateDone The task done date.
 * @param archived The task archived status.
 * @param creator The task creator.
 * @param assignees The task assignees.
 * @param groupAssignees The task group assignees
 * @param watchers The task watchers.
 * @param checklists The task checklists.
 * @param tags The task tags.
 * @param parent The task parent.
 * @param topLevelParent The task top level parent.
 * @param priority The task priority.
 * @param dueDate The task due date.
 * @param startDate The task start date.
 * @param points The task points.
 * @param timeEstimate The task time estimate.
 * @param timeSpent The task time spent.
 * @param customFields The task custom fields.
 * @param dependencies The task dependencies.
 * @param linkedTasks The task linked tasks.
 * @param locations The task locations.
 * @param teamId The task team ID.
 * @param url The task URL.
 * @param sharing The task sharing.
 * @param permissionLevel The task permission level.
 * @param list The task list.
 * @param project The task project.
 * @param folder The task folder.
 * @param space The task space.
 */
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


/**
 * Represents a task status.
 * @param id The status ID.
 * @param status The status.
 * @param color The status color.
 * @param orderindex The status order index.
 * @param type The status type.
 *
 */
@Serializable
data class Status(
    val id: String? = null,
    val status: String,
    val color: String? = null,
    val orderindex: Int? = null,
    val type: String? = null
)

/**
 * Represents a user.
 * @param id The user ID.
 * @param username The user username.
 * @param color The user color.
 * @param initials The user initials.
 * @param email The user email.
 * @param profilePicture The user profile picture.
 */
@Serializable
data class User(
    val id: Long,
    val username: String,
    val color: String? = null,
    val initials: String? = null,
    val email: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null
)

/**
 * Represents a task assignee.
 * @param id The assignee ID.
 * @param name The assignee name.
 * @param color The assignee color.
 * @param initials The assignee initials.
 * @param email The assignee email.
 * @param profilePicture The assignee profile picture.
 */
@Serializable
data class GroupAssignee(
    val id: String? = null,
    val name: String? = null,
    val color: String? = null,
    val initials: String? = null,
    val email: String? = null,
    @SerialName("profilePicture") val profilePicture: String? = null
)

/**
 * Represents a task checklist.
 * @param id The checklist ID.
 * @param name The checklist name.
 * @param orderindex The checklist order index.
 * @param tasks The checklist tasks.
 */
@Serializable
data class Checklist(
    val id: String? = null,
    val name: String? = null,
    val orderindex: Int? = null,
    val tasks: List<Task>? = null
)

/**
 * Represents a task tag.
 * @param id The tag ID.
 * @param name The tag name.
 * @param color The tag color.
 * @param orderindex The tag order index.
 */
@Serializable
data class Tag(
    val id: String? = null,
    val name: String? = null,
    val color: String? = null,
    val orderindex: Int? = null
)

/**
 * Represents a task priority.
 * @param id The priority ID.
 * @param priority The priority.
 * @param color The priority color.
 * @param orderindex The priority order index.
 */
@Serializable
data class Priority(
    val id: String? = null,
    val priority: String? = null,
    val color: String? = null,
    val orderindex: String? = null
)


/**
 * Represents a task custom field.
 * @param id The custom field ID.
 * @param name The custom field name.
 * @param type The custom field type.
 * @param value The custom field value.
 * @param options The custom field options.
 * @param orderindex The custom field order index.
 */
@Serializable
data class CustomField(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val value: String? = null,
    val options: List<String>? = null,
    val orderindex: Int? = null
)

/**
 * Represents a task dependency.
 * @param id The dependency ID.
 * @param name The dependency name.
 * @param status The dependency status.
 * @param orderindex The dependency order index.
 */
@Serializable
data class Dependency(
    val id: String? = null,
    val name: String? = null,
    val status: String? = null,
    val orderindex: Int? = null
)

/**
 * Represents a linked task.
 * @param id The linked task ID.
 * @param name The linked task name.
 * @param status The linked task status.
 * @param orderindex The linked task order index.
 */
@Serializable
data class LinkedTask(
    val id: String? = null,
    val name: String? = null,
    val status: String? = null,
    val orderindex: Int? = null
)

/**
 * Represents a task location.
 * @param id The location ID.
 * @param name The location name.
 * @param orderindex The location order index.
 * @param address The location address.
 * @param latitude The location latitude.
 * @param longitude The location longitude.
 */
@Serializable
data class Location(
    val id: String? = null,
    val name: String? = null,
    val orderindex: Int? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * Represents a task sharing.
 * @param public The public sharing status.
 * @param publicShareExpiresOn The public sharing expiration date.
 * @param publicFields The public fields.
 * @param token The sharing token.
 * @param seoOptimized The SEO optimized sharing status.
 */
@Serializable
data class Sharing(
    val public: Boolean? = null,
    @SerialName("public_share_expires_on") val publicShareExpiresOn: String? = null,
    @SerialName("public_fields") val publicFields: List<String>? = null,
    val token: String? = null,
    @SerialName("seo_optimized") val seoOptimized: Boolean? = null
)

/**
 * Represents a task list.
 * @param id The list ID.
 * @param name The list name.
 * @param access The list access.
 */
@Serializable
data class ListInfo(
    val id: String? = null,
    val name: String? = null,
    val access: Boolean? = null
)

/**
 * Represents a task project.
 * @param id The project ID.
 * @param name The project name.
 * @param hidden The project hidden status.
 * @param access The project access.
 */
@Serializable
data class ProjectInfo(
    val id: String? = null,
    val name: String? = null,
    val hidden: Boolean? = null,
    val access: Boolean? = null
)

/**
 * Represents a task folder.
 * @param id The folder ID.
 * @param name The folder name.
 * @param hidden The folder hidden status.
 * @param access The folder access.
 */
@Serializable
data class FolderInfo(
    val id: String? = null,
    val name: String? = null,
    val hidden: Boolean? = null,
    val access: Boolean? = null
)

/**
 * Represents a task space.
 * @param id The space ID.
 */
@Serializable
data class SpaceInfo(
    val id: String? = null
)
