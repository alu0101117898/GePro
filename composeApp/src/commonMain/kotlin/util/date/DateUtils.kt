package util.date
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(timestamp: Long, currentLocalDate: LocalDate): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val localDate = localDateTime.date

        val monthAbbreviations = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
        val day = localDateTime.dayOfMonth
        val month = monthAbbreviations[localDateTime.monthNumber - 1]

        if (localDate.year == currentLocalDate.year) {
            "$day $month"
        } else {
            "$day $month ${localDateTime.year}"
        }
    } catch (e: Exception) {
        "Fecha inválida"
    }
}