package com.survivalcoding.ai_court.presentation.verdict.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun FinalGaugeBar(
    title: String,
    leftName: String,
    rightName: String,
    leftScore: Int,
    rightScore: Int,
    modifier: Modifier = Modifier,
    maxScore: Int = 100, // 만점
    leftColor: Color = Color(0xFF60BFF9),   // 파랑
    rightColor: Color = Color(0xFFF44D4C),  // 핑크
    backColor: Color = AI_COURTTheme.colors.gray200,  // 회색
    barHeight: Dp = 19.dp,
    centerGap: Dp = 1.dp,
) {
    val safeMax = maxScore.coerceAtLeast(1)
    val l = leftScore.coerceIn(0, safeMax)
    val r = rightScore.coerceIn(0, safeMax)

    val leftRatio = l.toFloat() / safeMax
    val rightRatio = r.toFloat() / safeMax

    Column(modifier = modifier) {
        // 상단 텍스트: [왼쪽 이름 점수] [타이틀] [오른쪽 이름 점수]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${leftName} ${l}점",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = AI_COURTTheme.typography.Body_2
            )
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = AI_COURTTheme.typography.Body_2
            )
            Text(
                text = "${rightName} ${r}점",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = AI_COURTTheme.typography.Body_2
            )
        }
        Spacer(modifier = Modifier.height(2.dp))

        // 게이지
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            val radius = size.height / 2f
            val half = size.width / 2f
            val gapPx = centerGap.toPx().coerceAtLeast(0f)
            val gapHalf = gapPx / 2f

            // 배경
            drawRoundRect(
                color = backColor,
                cornerRadius = CornerRadius(radius, radius),
                size = size
            )

            // 중앙 갭 고려한 반쪽 최대 길이
            val maxHalf = (half - gapHalf).coerceAtLeast(0f)

            val leftW = (maxHalf * leftRatio).coerceIn(0f, maxHalf)
            val rightW = (maxHalf * rightRatio).coerceIn(0f, maxHalf)

            val leftEndX = half - gapHalf
            val rightStartX = half + gapHalf

            // 왼쪽 바
            if (leftW > 0f) {
                val x0 = leftEndX - leftW   // 왼쪽 바 시작
                val x1 = leftEndX           // 왼쪽 바 끝

                clipRect(left = x0, top = 0f, right = x1, bottom = size.height) {
                    // 왼쪽
                    drawCircle(
                        color = leftColor,
                        radius = radius,
                        center = Offset(x0 + radius, radius)
                    )

                    // 중앙 쪽 사각형
                    val rectLeft = x0 + radius
                    if (x1 > rectLeft) {
                        drawRect(
                            color = leftColor,
                            topLeft = Offset(rectLeft, 0f),
                            size = Size(x1 - rectLeft, size.height)
                        )
                    }
                }
            }

            // 오른쪽 바
            if (rightW > 0f) {
                val x0 = rightStartX           // 오른쪽 바 시작
                val x1 = rightStartX + rightW  // 오른쪽 바 끝

                clipRect(left = x0, top = 0f, right = x1, bottom = size.height) {
                    // 오른쪽
                    drawCircle(
                        color = rightColor,
                        radius = radius,
                        center = Offset(x1 - radius, radius)
                    )

                    // 중앙 쪽 사각형
                    val rectRight = x1 - radius
                    if (rectRight > x0) {
                        drawRect(
                            color = rightColor,
                            topLeft = Offset(x0, 0f),
                            size = Size(rectRight - x0, size.height)
                        )
                    }
                }
            }

            if (gapPx > 0f) {
                drawRect(
                    color = backColor,
                    topLeft = Offset(half - gapHalf, 0f),
                    size = Size(gapPx, size.height)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalGaugeBarPrev() {
    Column {
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 100,
            rightScore = 100
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 95,
            rightScore = 15,
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 80,
            rightScore = 70,
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 60,
            rightScore = 50,
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 10,
            rightScore = 9,
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 5,
            rightScore = 7,
        )
        FinalGaugeBar(
            title = "논리력",
            leftName = "박논리",
            rightName = "김논리",
            leftScore = 0,
            rightScore = 2,
        )
    }
}