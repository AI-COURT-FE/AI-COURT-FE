package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun UntilDoneComponent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000),
                shape = RoundedCornerShape(27.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF755139),
                shape = RoundedCornerShape(27.dp)
            )
            .width(337.dp)
            .height(302.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(27.dp)
            )
            .clip(RoundedCornerShape(27.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = AI_COURTTheme.colors.navy,
                modifier = Modifier.size(85.dp),
                strokeWidth = 10.dp
            )
            Spacer(Modifier.height(35.dp))
            Text(
                text = "AI가 판결 중입니다...",
                style = AI_COURTTheme.typography.Body_2
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun UntilDoneComponentPrev() {
    UntilDoneComponent()
}