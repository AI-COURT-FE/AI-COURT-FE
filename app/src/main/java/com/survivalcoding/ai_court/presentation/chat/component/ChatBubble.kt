
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme
@Composable
fun ChatBubble(message: ChatMessage, isMine: Boolean) {
    val bubbleShape = if (isMine) {
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomStart = 10.dp,
            bottomEnd = 0.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 10.dp,
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {

        // ✅ 상대 메시지일 때만 프로필 표시
        if (!isMine) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFFA8A8))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFF2121),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = "프로필",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.padding(start = 6.dp))
        }

        // ✅ 말풍선은 항상 표시 (내 메시지도 여기로 옴)
        Column(modifier = if (!isMine) Modifier.padding(start = 6.dp) else Modifier) {

            // ✅ 상대 메시지일 때만 닉네임 표시
            if (!isMine) {
                Text(
                    text = message.senderNickname,
                    style = AI_COURTTheme.typography.Body_2
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(if (isMine) Color.White else Color(0xFFE7B978))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 260.dp)
            ) {
                Text(
                    text = message.content,
                    style = AI_COURTTheme.typography.Caption_4
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ChatBubblePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 상대 메시지 (왼쪽)
        ChatBubble(
            message = ChatMessage(
                id = "1",
                roomCode = "ROOM123",
                senderId = "user_b",
                senderNickname = "박논리",
                content = "솔직히 네가 늦은 건 맞잖아\n사과는 해야지",
                timestamp = System.currentTimeMillis(),
                isMyMessage = false
            ),
            isMine = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 내 메시지 (오른쪽)
        ChatBubble(
            message = ChatMessage(
                id = "2",
                roomCode = "ROOM123",
                senderId = "user_a",
                senderNickname = "김논리",
                content = "아니 차가 막힌 걸 어쩔 수 없잖아",
                timestamp = System.currentTimeMillis(),
                isMyMessage = true
            ),
            isMine = true
        )
    }
}
