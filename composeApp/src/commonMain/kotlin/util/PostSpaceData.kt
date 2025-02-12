package util

import kotlinx.serialization.Serializable

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
    @Serializable
    data class DueDates(
        val enabled: Boolean,
        val start_date: Boolean,
        val remap_due_dates: Boolean,
        val remap_closed_due_date: Boolean
    )

    @Serializable
    data class TimeTracking(
        val enabled: Boolean
    )

    @Serializable
    data class Tags(
        val enabled: Boolean
    )

    @Serializable
    data class TimeEstimates(
        val enabled: Boolean
    )

    @Serializable
    data class Checklists(
        val enabled: Boolean
    )

    @Serializable
    data class CustomFields(
        val enabled: Boolean
    )

    @Serializable
    data class RemapDependencies(
        val enabled: Boolean
    )

    @Serializable
    data class DependencyWarning(
        val enabled: Boolean
    )

    @Serializable
    data class Portfolios(
        val enabled: Boolean
    )
}

@Serializable
data class PostSpaceData(
    val name: String,
    val multiple_assignees: Boolean,
    val features: SpaceFeatures
)