package util.functions.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommentItem(
    comment: data.Comment,
    onEdit: (data.Comment) -> Unit,
    onDelete: (data.Comment) -> Unit,
) {
    var hovered by remember { mutableStateOf(false) }

    val hoverModifier = Modifier.onPointerEvent(
        PointerEventType.Move
    ) {
    }
        .onPointerEvent(PointerEventType.Enter) {
            hovered = true
        }
        .onPointerEvent(PointerEventType.Exit) {
            hovered = false
        }

    val initials = comment.user?.initials?.uppercase()
    val userName = comment.user?.username
    val dateLong = comment.date.toLong()
    val localDateTime = Instant.fromEpochMilliseconds(dateLong).toLocalDateTime(TimeZone.currentSystemDefault())
    val dateText = "${localDateTime.hour}:${localDateTime.minute} - ${localDateTime.dayOfMonth}/${localDateTime.monthNumber}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(hoverModifier),
        elevation = 1.dp,
        backgroundColor = Color.White
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF7C4DFF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (initials != null) {
                    Text(
                        text = initials,
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (userName != null) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.caption.copy(color = Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comment.comment_text,
                    style = MaterialTheme.typography.body2.copy(color = Color.Black)
                )
            }

            if (hovered) {
                Row {
                    Text(
                        text = "✏️",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onEdit(comment) }
                    )
                    Text(
                        text = "🗑️",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onDelete(comment) }
                    )
                }
            }
        }
    }
}
