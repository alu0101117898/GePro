package util

import androidx.compose.ui.graphics.Color

fun parseColor(colorHex: String): Color {
    return try {
        val cleanColor = colorHex.replace("#", "")
        val (a, r, g, b) = when (cleanColor.length) {
            6 -> listOf("FF", cleanColor.substring(0..1), cleanColor.substring(2..3), cleanColor.substring(4..5))
            8 -> listOf(cleanColor.substring(0..1), cleanColor.substring(2..3), cleanColor.substring(4..5), cleanColor.substring(6..7))
            else -> return Color.Black
        }
        Color(
            alpha = a.hexToInt(),
            red = r.hexToInt(),
            green = g.hexToInt(),
            blue = b.hexToInt()
        )
    } catch (e: Exception) {
        Color.Black // Fallback a negro si hay error
    }
}

private fun String.hexToInt() = this.toInt(16)