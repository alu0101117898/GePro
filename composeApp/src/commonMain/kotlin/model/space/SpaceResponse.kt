package model.space

import kotlinx.serialization.Serializable

@Serializable
data class SpaceResponse(
    val spaces: List<Space>
)