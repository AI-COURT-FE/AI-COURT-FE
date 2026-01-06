package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.domain.model.Verdict

@Composable
fun VerdictDialog(
    verdict: Verdict,
    onDismiss: () -> Unit,
    onShareResult: () -> Unit = {},
    onGoToMain: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 타이틀
                Text(
                    text = "⚖️ 최종 판결",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 승자 발표
                Text(
                    text = "🏆 승자",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = verdict.winnerNickname,
                    color = Color(0xFF4ECDC4),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 점수
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreCard(
                        label = "User A",
                        score = verdict.scoreA,
                        color = Color(0xFF4ECDC4)
                    )
                    ScoreCard(
                        label = "User B",
                        score = verdict.scoreB,
                        color = Color(0xFFFF6B6B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = Color(0xFF3D3D5C))

                Spacer(modifier = Modifier.height(16.dp))

                // 판결 이유
                Text(
                    text = "📋 판결 이유",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = verdict.reason,
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3줄 요약
                Text(
                    text = "📝 요약",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                verdict.summary.forEachIndexed { index, line ->
                    Text(
                        text = "${index + 1}. $line",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼들
                CourtButton(
                    text = "메인으로 돌아가기",
                    onClick = onGoToMain,
                    containerColor = Color(0xFF4ECDC4)
                )
            }
        }
    }
}

@Composable
private fun ScoreCard(
    label: String,
    score: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = Color(0xFF2D2D44),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = score.toString(),
            color = color,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VerdictDialogPreview() {
    MaterialTheme {
        val sampleVerdict = Verdict(
            winner = "A",
            winnerNickname = "Ayoung",
            scoreA = 92,
            scoreB = 78,
            reason = "증거의 신뢰성과 진술의 일관성을 종합했을 때 User A의 주장이 더 설득력 있다고 판단됩니다.",
            summary = listOf(
                "핵심 증거가 User A 주장과 일치",
                "User B 진술의 일부가 모순됨",
                "정황상 User A 설명이 더 합리적임"
            )
        )

        VerdictDialog(
            verdict = sampleVerdict,
            onDismiss = {},
            onShareResult = {},
            onGoToMain = {}
        )
    }
}

/*
data class Verdict(
    val winner: String,
    val winnerNickname: String,
    val scoreA: Int,
    val scoreB: Int,
    val reason: String,
    val summary: List<String> // 3줄 요약
)
 */