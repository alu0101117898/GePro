package model.space

import kotlinx.serialization.Serializable

@Serializable
data class Space(
    val id: String,
    val name: String,
    val status: String? = null,
    val color: String? = null,
    val private: Boolean? = null
)