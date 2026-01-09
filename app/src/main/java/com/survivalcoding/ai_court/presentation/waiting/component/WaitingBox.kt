package com.survivalcoding.ai_court.presentation.waiting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme
import kotlin.math.abs

@Composable
fun WaitingBox(
    roomCode: String,
    onCopyRoomCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(452.dp)
            .clip(cardShape)
            .background(color = Color(0xFFEDEDED))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(
                    color = AI_COURTTheme.colors.darkNavy,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
        )

        Column(
            modifier = Modifier
                .padding(top = 53.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_waiting_room),
                contentDescription = "대기 아이콘",
                modifier = Modifier.size(69.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "피고 소환 대기 중",
                style = AI_COURTTheme.typography.Body_1,
                color = AI_COURTTheme.colors.black
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "상대방에게 사건번호 또는 링크를\n전송하여 법정에 입장시키세요.",
                style = AI_COURTTheme.typography.Caption_regular
            )

            Spacer(modifier = Modifier.height(21.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(122.dp)
                    .background(Color(0xFFEDEDED), innerShape)
                    .border(1.dp, Color(0xFFBABABA), innerShape)
                    .padding(horizontal = 19.dp)
            ) {
                Column(

                ) {
                    Text(
                        text = "사건번호",
                        style = AI_COURTTheme.typography.Caption_regular,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    AutoResizeText(
                        text = roomCode,
                        style = AI_COURTTheme.typography.Title_1,
                        maxFontSize = AI_COURTTheme.typography.Title_1.fontSize,
                        minFontSize = 14.sp,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = "복사하기",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(52.dp)
                        .clickable { onCopyRoomCode() }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(54.dp)
                    .border(1.dp, AI_COURTTheme.colors.black, innerShape)
                    .background(AI_COURTTheme.colors.white, innerShape)
            ) {
                // 왼쪽 아이콘 영역(고정 폭)
                Box(
                    modifier = Modifier
                        .width(21.dp + 24.dp) // start padding(21) + icon(24)
                        .padding(start = 21.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = "공유하기",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 가운데 텍스트 영역(가변 폭) - 진짜 중앙
                Text(
                    text = "초대링크 복사하기",
                    style = AI_COURTTheme.typography.Body_2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // 오른쪽 더미 영역(왼쪽과 동일 폭) -> 텍스트가 Row 정중앙에 오게 함
                Spacer(modifier = Modifier.width(21.dp + 24.dp))
            }

        }
    }
}

@Composable
private fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxFontSize: TextUnit,
    minFontSize: TextUnit = 10.sp,
    maxLines: Int = 1
) {
    BoxWithConstraints(modifier = modifier) {
        val measurer = rememberTextMeasurer()
        val widthPx = constraints.maxWidth

        var targetSize by remember(text, widthPx, maxFontSize, minFontSize) {
            mutableStateOf(maxFontSize)
        }

        LaunchedEffect(text, widthPx, maxFontSize, minFontSize) {
            var lo = minFontSize.value
            var hi = maxFontSize.value
            var best = lo

            repeat(18) {
                val mid = (lo + hi) / 2f
                val result = measurer.measure(
                    text = text,
                    style = style.copy(fontSize = mid.sp),
                    maxLines = maxLines,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
                val fits = !result.hasVisualOverflow
                if (fits) {
                    best = mid
                    lo = mid
                } else {
                    hi = mid
                }
                if (abs(hi - lo) < 0.25f) return@repeat
            }
            targetSize = best.sp
        }

        Text(
            text = text,
            style = style.copy(fontSize = targetSize),
            maxLines = maxLines,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}
