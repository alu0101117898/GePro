package model.space

import data.SpaceData
import data.SpaceFeatures
import data.StatusData

/**
 * Extension function to convert a Space object to a SpaceData object.
 * @return The SpaceData object.
 */
fun Space.toSpaceData(): SpaceData {
    return SpaceData(
        name = this.name,
        multiple_assignees = false,
        features = SpaceFeatures(
            due_dates = SpaceFeatures.DueDates(
                enabled = true,
                start_date = true,
                remap_due_dates = true,
                remap_closed_due_date = false
            ),
            time_tracking = SpaceFeatures.TimeTracking(enabled = true),
            tags = SpaceFeatures.Tags(enabled = true),
            time_estimates = SpaceFeatures.TimeEstimates(enabled = true),
            checklists = SpaceFeatures.Checklists(enabled = true),
            custom_fields = SpaceFeatures.CustomFields(enabled = true),
            remap_dependencies = SpaceFeatures.RemapDependencies(enabled = true),
            dependency_warning = SpaceFeatures.DependencyWarning(enabled = true),
            portfolios = SpaceFeatures.Portfolios(enabled = true)
        ),
        statuses = listOf(
            StatusData(status = "to do", type = "open", orderindex = 0, color = "#87909e"),
            StatusData(status = "in progress", type = "custom", orderindex = 1, color = "#5f55ee"),
            StatusData(status = "complete", type = "closed", orderindex = 2, color = "#008844")
        )
    )
}