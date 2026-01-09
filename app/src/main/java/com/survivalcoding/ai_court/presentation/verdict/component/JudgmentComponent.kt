package com.survivalcoding.ai_court.presentation.verdict.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion.Unspecified
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme
import kotlin.math.abs

@Composable
fun JudgmentComponent(
    modifier: Modifier = Modifier,
    winnerNickname: String,
    loserNickname: String = "",
    reason: String,
    summary: List<String>,
    cardHeight: Dp = 600.dp,
    gauges: @Composable ColumnScope.() -> Unit = {},
) {
    val cardShape = RoundedCornerShape(28.dp)
    val s1 = summary.getOrNull(0).orEmpty()
    val s2 = summary.getOrNull(1).orEmpty()
    val ribbonColors = listOf(
        Color(0xFFFFAC33),
        Color(0xFF99671F)
    )
    val scrollState = rememberScrollState()

    val headerText: AnnotatedString = buildAnnotatedString {
        append("원고 ")
        withStyle(SpanStyle(color = AI_COURTTheme.colors.blue)) { append(winnerNickname) }
        append(" 승소")
    }

    Box(
        modifier = modifier // modifier 적용
            .fillMaxWidth()
            .then(
                if (cardHeight != Unspecified) Modifier.height(cardHeight)
                else Modifier
            )
            .clip(cardShape)
            .background(Color.White)
    ) {
        // 상단 그라데이션
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    brush = Brush.verticalGradient(ribbonColors)
                )
        )

        // 실제 내용
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 16.dp + 34.dp,
                    start = 25.dp,
                    end = 25.dp,
                    bottom = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            // 상단 Image
            Image(
                painter = painterResource(id = R.drawable.ic_balance),
                contentDescription = "저울 이미지",
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .size(53.dp)
            )

            // 판결 헤더 (원고 박논리 승소)
            AutoResizeText(
                text = headerText,
                modifier = Modifier
                    .fillMaxWidth(),
                style = AI_COURTTheme.typography.Title_2.copy(textAlign = TextAlign.Center),
                maxFontSize = AI_COURTTheme.typography.Title_2.fontSize, // 여기서 시작
                minFontSize = 18.sp,
            )

            Spacer(Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 게이지 (외부에서 2개 꽂기)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) { gauges() }

                // AI comment
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AI_COURTTheme.colors.white),
                    border = BorderStroke(1.dp, AI_COURTTheme.colors.gray400),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "AI comment",
                            style = AI_COURTTheme.typography.Body_2,
                            color = AI_COURTTheme.colors.gray900
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = reason,
                            style = AI_COURTTheme.typography.Body_4,
                            color = AI_COURTTheme.colors.black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AI_COURTTheme.colors.gray400),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        summary.take(2).forEachIndexed { idx, line ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (idx == 0) R.drawable.ic_check else R.drawable.ic_cross_mark
                                    ),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = line,
                                    style = AI_COURTTheme.typography.Body_2,
                                    color = AI_COURTTheme.colors.gray900,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (idx == 0) Spacer(Modifier.height(20.dp))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
fun AutoResizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxFontSize: TextUnit,
    minFontSize: TextUnit = 12.sp,
    maxLines: Int = 1
) {
    BoxWithConstraints(modifier = modifier) {
        val measurer = rememberTextMeasurer()
        val widthPx = constraints.maxWidth

        var targetSize by remember(text, widthPx) { mutableStateOf(maxFontSize) }

        LaunchedEffect(text, widthPx) {
            var lo = minFontSize.value
            var hi = maxFontSize.value
            var best = lo

            // binary search로 "한 줄에 들어가는 최대 폰트" 찾기
            repeat(18) {
                val mid = (lo + hi) / 2f
                val result = measurer.measure(
                    text = text,
                    style = style.copy(fontSize = mid.sp),
                    maxLines = maxLines
                )

                val fits = !result.hasVisualOverflow && result.size.width <= widthPx
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
//            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JudgmentComponentPreview() {
    JudgmentComponent(
        winnerNickname = "박논리논리ㄴ노닌ㅇ리낭런오리나어린알안리ㅓㅇ나ㅓ리ㅏ너ㅣ러니ㅏ어리넝논리",
        loserNickname = "김논리",
        reason = "박논리님의 승리입니다.\n\n논리는 대등했으나, 법정 모독(비속어 사용)으로 박논리 승소!",
        summary = listOf(
            "원고는 구체적 사유를 들어\n지각을 소명함",
            "피고는 논리적 반박 대신\n감정적 비난으로 일관함"
        ),
        gauges = {
            FinalGaugeBar(
                title = "논리력",
                leftName = "박논리",
                rightName = "김논리",
                leftScore = 95,
                rightScore = 15,
            )
            FinalGaugeBar(
                title = "공감력",
                leftName = "박논리",
                rightName = "김논리",
                leftScore = 5,
                rightScore = 80,
            )
        }
    )
}

/*
data class FinalVerdict(
    val winnerNickname: String,
    val loserNickname: String,

    val logicA: Int,
    val logicB: Int,
    val empathyA: Int,
    val empathyB: Int,

    val reason: String,
    val summary: List<String>
)
 */