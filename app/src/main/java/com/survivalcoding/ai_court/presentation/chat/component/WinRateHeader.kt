package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

