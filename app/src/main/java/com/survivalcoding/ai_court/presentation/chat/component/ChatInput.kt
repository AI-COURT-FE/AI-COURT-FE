
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFF000000),
                shape = RoundedCornerShape(size = 16.dp)
            )
            .padding(horizontal = 25.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 15.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "변론 내용을 입력하세요",
                            style = AI_COURTTheme.typography.Body_2,
                            color = Color(0xFF8C8C8C),
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInputPrev() {
    var text by remember { mutableStateOf("") }

    ChatInput(
        value = text,
        onValueChange = { text = it },
        onSendClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
