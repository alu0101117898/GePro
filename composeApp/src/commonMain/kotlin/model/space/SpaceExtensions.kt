package model.space

import data.SpaceData
import data.SpaceFeatures

fun Space.toSpaceData(): SpaceData {
    return SpaceData(
        name = this.name,
        multiple_assignees = false, // Puedes modificar este valor según corresponda
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
        )
    )
}