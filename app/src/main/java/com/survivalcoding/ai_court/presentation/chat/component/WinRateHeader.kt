package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.core.component.WinRateGaugeBar
import com.survivalcoding.ai_court.domain.model.WinRate

@Composable
fun WinRateHeader(
    myNickname: String,
    opponentNickname: String,
    winRate: WinRate,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E))
            .padding(vertical = 12.dp)
    ) {
        WinRateGaugeBar(
            userAName = myNickname,
            userBName = opponentNickname,
            userAScore = winRate.userAScore,
            userBScore = winRate.userBScore
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WinRateHeaderPrev() {
    MaterialTheme {
        WinRateHeader(
            myNickname = "나",
            opponentNickname = "너",
            winRate = WinRate(userAScore = 62, userBScore = 38),
            modifier = Modifier.padding(0.dp)
        )
    }
}

/*
data class WinRate(
    val userAScore: Int,
    val userBScore: Int
) {
    val total: Int get() = userAScore + userBScore
    val userAPercentage: Float get() = if (total > 0) userAScore.toFloat() / total else 0.5f
    val userBPercentage: Float get() = if (total > 0) userBScore.toFloat() / total else 0.5f
}
 */