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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JudgmentComponent(
    modifier: Modifier = Modifier,
    winnerNickname: String,
    plaintiffNickname: String,
    defendantNickname: String,
    judgmentComment: String,
    winnerReason: String,
    loserReason: String,
    cardHeight: Dp = 600.dp,
    gauges: @Composable ColumnScope.() -> Unit = {},
) {
    val cardShape = RoundedCornerShape(28.dp)
    val ribbonColors = listOf(Color(0xFFFFAC33), Color(0xFF99671F))
    val scrollState = rememberScrollState()

    val winnerRoleLabel = if (winnerNickname == plaintiffNickname) "원고" else "피고"

    val headerText: AnnotatedString = buildAnnotatedString {
        append("$winnerRoleLabel ")
        withStyle(SpanStyle(color = AI_COURTTheme.colors.blue)) { append(winnerNickname) }
        append(" 승소")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(cardShape)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(brush = Brush.verticalGradient(ribbonColors))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 25.dp, end = 25.dp, bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_balance),
                contentDescription = "저울 이미지",
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .size(53.dp)
            )

            AutoResizeText(
                text = headerText,
                modifier = Modifier.fillMaxWidth(),
                style = AI_COURTTheme.typography.Title_2.copy(textAlign = TextAlign.Center),
                maxFontSize = AI_COURTTheme.typography.Title_2.fontSize,
                minFontSize = 18.sp,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "원고: $plaintiffNickname   |   피고: $defendantNickname",
                style = AI_COURTTheme.typography.Body_4,
                color = AI_COURTTheme.colors.gray900
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) { gauges() }

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
                            text = judgmentComment,
                            style = AI_COURTTheme.typography.Body_4,
                            color = AI_COURTTheme.colors.black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AI_COURTTheme.colors.gray400),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        ReasonRow(iconRes = R.drawable.ic_check, text = winnerReason)
                        Spacer(Modifier.height(20.dp))
                        ReasonRow(iconRes = R.drawable.ic_cross_mark, text = loserReason)
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ReasonRow(iconRes: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = AI_COURTTheme.typography.Body_2,
            color = AI_COURTTheme.colors.gray900,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AutoResizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 14.sp,
    maxLines: Int = 1,
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Box(modifier = modifier) {
        val density = LocalDensity.current

        Text(
            text = text,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            style = style.copy(fontSize = fontSize),
            onTextLayout = { result ->
                // 텍스트가 잘리면 폰트 줄이기
                if (result.didOverflowWidth && fontSize > minFontSize) {
                    val current = with(density) { fontSize.toPx() }
                    val nextPx = (current * 0.92f) // 8%씩 감소
                    fontSize = with(density) { nextPx.toSp() }
                    readyToDraw = false
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}