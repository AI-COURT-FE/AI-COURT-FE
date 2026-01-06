package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.domain.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isMyMessage = message.isMyMessage
    val bubbleColor = if (isMyMessage) Color(0xFF4ECDC4) else Color(0xFF2D2D44)
    val textColor = if (isMyMessage) Color(0xFF0F0F1A) else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
        ) {
            // 닉네임 (상대방 메시지만)
            if (!isMyMessage) {
                Text(
                    text = message.senderNickname,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }

            // 메시지 버블
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = bubbleColor,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMyMessage) 16.dp else 4.dp,
                            bottomEnd = if (isMyMessage) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 15.sp
                )
            }

            // 시간
            Text(
                text = formatTime(message.timestamp),
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

