
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun ChatTopBar(
    roomCode: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(AI_COURTTheme.colors.darkNavy)
    ) {

        Text(
            text = "법정 \n사건번호 $roomCode",
            modifier = Modifier.align(Alignment.Center),
            style = AI_COURTTheme.typography.Body_3,
            color = AI_COURTTheme.colors.white,
                    textAlign = TextAlign.Center
        )

        // 왼쪽 뒤로가기 아이콘
        Image(
            painter = painterResource(R.drawable.ic_back_vector),
            contentDescription = "뒤로 가기",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 21.dp)
                .width(12.dp)
                .height(24.dp)
                .clickable { onNavigateBack() }
        )
    }
}

