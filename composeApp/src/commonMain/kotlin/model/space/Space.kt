package model.space

import kotlinx.serialization.Serializable


/**
 * Data class for space features in ClickUp.
 * @id Data class for space id in ClickUp.
 * @name Data class for space name in ClickUp.
 * @status Data class for space status in ClickUp.
 * @color Data class for space color in ClickUp.
 * @private Data class for space privacy in ClickUp.
 */
@Serializable
data class Space(
    val id: String,
    val name: String,
    val status: String? = null,
    val color: String? = null,
    val private: Boolean? = null
)