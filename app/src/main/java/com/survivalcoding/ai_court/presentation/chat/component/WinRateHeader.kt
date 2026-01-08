import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme


fun calcPercent(left: Int, right: Int): Float {
    val total = left + right
    if (total <= 0) return 0.5f // 둘다 0이면 50:50로
    return left.toFloat() / total.toFloat() // 0.0 ~ 1.0
}

@Composable
fun WinRateHeader(
    leftName: String,
    rightName: String,
    leftScore: Int,
    rightScore: Int,
    modifier: Modifier = Modifier
) {

    val leftRatio = remember(leftScore, rightScore) {
        calcPercent(leftScore, rightScore) // 0~1
    }
    val rightRatio = 1f - leftRatio

    val leftPct = (leftRatio * 100).toInt()
    val rightPct = 100 - leftPct

    val animatedRatio by animateFloatAsState(
        targetValue = leftRatio,
        label = "ratio"
    )

    Column(modifier
        .height(143.dp)
        .fillMaxSize()   // 부모 전체 크기
        .background(color = AI_COURTTheme.colors.white),
        horizontalAlignment = Alignment.CenterHorizontally,) {

        // 텍스트
        Row(
            Modifier
                .padding(top=7.dp)
                .fillMaxWidth()
        ) {
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    leftName,
                    style = AI_COURTTheme.typography.Body_1,
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "${leftPct}%",
                    style = AI_COURTTheme.typography.Body_1,
                    color = Color(0xFF0C2F86)
                )
            }
            Text(
                "VS",
                style = AI_COURTTheme.typography.Body_1,
            )
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    rightName,
                    style = AI_COURTTheme.typography.Body_1,
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "${rightPct}%",
                    style = AI_COURTTheme.typography.Body_1,
                    color = Color(0xFF9E9E9E)
                )
            }
        }

        Spacer(Modifier.height(5.dp))
        // 막대 (왼쪽이 차오르는 스타일)
        Box(
            Modifier
                .fillMaxWidth()
                .height(19.dp)
                .clip(RoundedCornerShape(37.dp))
                .background(Color(0xFFD9D9D9))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(leftRatio)
                    .background(Color(0xFF1F2A7A))
            )
        }

        Spacer(modifier= Modifier.height(11.dp))
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000)
                )
                .width(125.dp)
                .height(40.dp)
                .background(color = Color(0xFF755139), shape = RoundedCornerShape(size = 16.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_judge_mini),
                contentDescription = "작은 판결 아이콘"
            )
            Text(
                "판결요청",
                style= AI_COURTTheme.typography.Body_2,
                color = AI_COURTTheme.colors.white
            )

        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun WinRateHeaderPreview_Static() {
    WinRateHeader(
        leftName = "김논리",
        rightName = "박논리",
        leftScore = 60,
        rightScore = 40,
        modifier = Modifier.padding(16.dp)
    )
}
