package util
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(timestamp: Long, currentYear: Int): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val monthAbbreviations = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
        val day = localDateTime.dayOfMonth
        val month = monthAbbreviations[localDateTime.monthNumber - 1]

        if (localDateTime.year == currentYear) {
            "$day $month"
        } else {
            "$day $month ${localDateTime.year}"
        }
    } catch (e: Exception) {
        "Fecha inválida"
    }
}