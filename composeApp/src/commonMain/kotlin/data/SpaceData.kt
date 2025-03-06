@file:Suppress("PropertyName")

package data

import kotlinx.serialization.Serializable


/**
 * Data class for space features in ClickUp.
 * @property due_dates Data class for due dates in ClickUp.
 * @property time_tracking Data class for time tracking in ClickUp.
 * @property tags Data class for tags in
 * @property time_estimates Data class for time estimates in ClickUp.
 * @property checklists Data class for checklists in ClickUp.
 * @property custom_fields Data class for custom fields in ClickUp.
 * @property remap_dependencies Data class for remap dependencies in ClickUp.
 * @property dependency_warning Data class for dependency warning in ClickUp.
 * @property portfolios Data class for portfolios in ClickUp.
 */
@Serializable
data class SpaceFeatures(
    val due_dates: DueDates,
    val time_tracking: TimeTracking,
    val tags: Tags,
    val time_estimates: TimeEstimates,
    val checklists: Checklists,
    val custom_fields: CustomFields,
    val remap_dependencies: RemapDependencies,
    val dependency_warning: DependencyWarning,
    val portfolios: Portfolios
) {
    /**
     * Data class for due dates in ClickUp.
     * @property enabled Whether due dates are enabled.
     * @property start_date Whether start dates are enabled.
     * @property remap_due_dates Whether due dates are remapped.
     * @property remap_closed_due_date Whether closed due dates are remapped.
     */
    @Serializable
    data class DueDates(
        val enabled: Boolean,
        val start_date: Boolean,
        val remap_due_dates: Boolean,
        val remap_closed_due_date: Boolean
    )

    /**
     * Data class for time tracking in ClickUp.
     * @property enabled Whether time tracking is enabled.
     */
    @Serializable
    data class TimeTracking(
        val enabled: Boolean
    )

    /**
     * Data class for tags in
     * @property enabled Whether tags are enabled.
     */
    @Serializable
    data class Tags(
        val enabled: Boolean
    )


    /**
     * Data class for time estimates in ClickUp.
     * @property enabled Whether time estimates are enabled.
     */
    @Serializable
    data class TimeEstimates(
        val enabled: Boolean
    )

    /**
     * Data class for checklists in ClickUp.
     * @property enabled Whether checklists are enabled.
     */
    @Serializable
    data class Checklists(
        val enabled: Boolean
    )

    /**
     * Data class for custom fields in ClickUp.
     * @property enabled Whether custom fields are enabled.
     */
    @Serializable
    data class CustomFields(
        val enabled: Boolean
    )

    /**
     * Data class for remap dependencies in ClickUp.
     * @property enabled Whether remap dependencies are enabled.
     */
    @Serializable
    data class RemapDependencies(
        val enabled: Boolean
    )

    /**
     * Data class for dependency warning in ClickUp.
     * @property enabled Whether dependency warning is enabled.
     */
    @Serializable
    data class DependencyWarning(
        val enabled: Boolean
    )

    /**
     * Data class for portfolios in ClickUp.
     * @property enabled Whether portfolios are enabled.
     */
    @Serializable
    data class Portfolios(
        val enabled: Boolean
    )
}

/**
 * Data class for space data in ClickUp.
 * @property name The name of the space.
 * @property multiple_assignees Whether multiple assignees are allowed.
 * @property features The features of the space.
 * @property statuses The statuses of the space.
 */
@Serializable
data class SpaceData(
    val name: String,
    val multiple_assignees: Boolean,
    val features: SpaceFeatures,
    val statuses: List<StatusData>

)

/**
 * Data class for status data in ClickUp.
 * @property status The status of the data.
 * @property type The type of the data.
 * @property orderindex The order index of the data.
 * @property color The color of the data.
 */
@Serializable
data class StatusData(
    val status: String,
    val type: String,
    val orderindex: Int,
    val color: String
)