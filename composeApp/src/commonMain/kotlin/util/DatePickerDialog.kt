package util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun DatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val currentDate = initialDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault())

    var selectedDate by remember { mutableStateOf(currentDate) }
    var currentMonth by remember { mutableStateOf(currentDate.month) }
    var currentYear by remember { mutableStateOf(currentDate.year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar fecha") },
        text = {
            Column {
                // Controles de mes/año
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        if (currentMonth == Month.JANUARY) {
                            currentYear--
                            currentMonth = Month.DECEMBER
                        } else {
                            currentMonth = java.time.Month.entries.toTypedArray()[currentMonth.ordinal - 1]
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Mes anterior")
                    }

                    Text(
                        text = "${currentMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $currentYear",
                        style = MaterialTheme.typography.h6
                    )

                    IconButton(onClick = {
                        if (currentMonth == Month.DECEMBER) {
                            currentYear++
                            currentMonth = Month.JANUARY
                        } else {
                            currentMonth = java.time.Month.entries.toTypedArray()[currentMonth.ordinal + 1]
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Mes siguiente")
                    }
                }

                // Calendario
                CalendarGrid(
                    year = currentYear,
                    month = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(
                        LocalDate(
                            year = selectedDate.year,
                            month = selectedDate.month,
                            dayOfMonth = selectedDate.dayOfMonth
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Month,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = LocalDate(year, month, 1)
    val daysInMonth = firstDayOfMonth.daysInMonth()
    val startDay = (firstDayOfMonth.dayOfWeek.ordinal + 6) % 7 // Lunes como primer día

    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val daysList = (1..daysInMonth).map { day ->
        LocalDate(year, month, day)
    }

    Column {
        // Días de la semana
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f).padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Días del mes
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(200.dp)
        ) {
            items(startDay) { /* Espacios vacíos al inicio */ }

            items(daysList) { date ->
                val isSelected = date == selectedDate

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .background(
                            color = if (isSelected) Color.LightGray else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = date.dayOfMonth.toString())
                }
            }
        }
    }
}

// Extensión para obtener días del mes
fun LocalDate.daysInMonth(): Int {
    return when (month) {
        Month.FEBRUARY -> if (year % 4 == 0) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
}