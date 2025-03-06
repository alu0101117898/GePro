package model.space

import kotlinx.serialization.Serializable

/**
 * Data class for SpaceResponse
 * @property spaces List of Space
 */
@Serializable
data class SpaceResponse(
    val spaces: List<Space>
)