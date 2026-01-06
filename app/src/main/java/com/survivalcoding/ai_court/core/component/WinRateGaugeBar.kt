package com.survivalcoding.ai_court.core.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WinRateGaugeBar(
    userAName: String,
    userBName: String,
    userAScore: Int,
    userBScore: Int,
    modifier: Modifier = Modifier
) {
    val total = (userAScore + userBScore).coerceAtLeast(1)
    val userARatio by animateFloatAsState(
        targetValue = userAScore.toFloat() / total,
        animationSpec = tween(durationMillis = 500),
        label = "userARatio"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 이름 및 점수 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$userAName ($userAScore)",
                color = Color(0xFF4ECDC4),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "($userBScore) $userBName",
                color = Color(0xFFFF6B6B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // 게이지 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2D2D44))
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // User A 영역 (왼쪽 - 민트)
                Box(
                    modifier = Modifier
                        .weight(userARatio.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4ECDC4),
                                    Color(0xFF44A08D)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (userARatio > 0.15f) {
                        Text(
                            text = "${(userARatio * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // User B 영역 (오른쪽 - 레드)
                Box(
                    modifier = Modifier
                        .weight((1f - userARatio).coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE55039),
                                    Color(0xFFFF6B6B)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if ((1f - userARatio) > 0.15f) {
                        Text(
                            text = "${((1f - userARatio) * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

