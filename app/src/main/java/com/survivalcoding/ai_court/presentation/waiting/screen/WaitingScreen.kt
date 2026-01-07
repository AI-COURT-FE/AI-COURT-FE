package com.survivalcoding.ai_court.presentation.waiting.screen

import ChatTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.presentation.waiting.component.InfoBanner
import com.survivalcoding.ai_court.presentation.waiting.component.WaitingBox
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingScreen(
    roomCode: String,
    onNavigateToChat: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier= Modifier.background(color= AI_COURTTheme.colors.cream),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ChatTopBar()
        Spacer(modifier= Modifier.height(68.dp))
        WaitingBox()
        Spacer(modifier= Modifier.height(25.dp))
        InfoBanner()
    }

}

@Preview(showBackground = true)
@Composable
fun WaitingScreenPreview() {
    AI_COURTTheme {
        WaitingScreen(
            roomCode = "ABCD1234",
            onNavigateToChat = {},
            onNavigateBack = {}
        )
    }
}
