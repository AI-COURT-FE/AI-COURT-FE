package com.survivalcoding.ai_court.presentation.waiting.screen

import ChatTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.presentation.waiting.component.InfoBanner
import com.survivalcoding.ai_court.presentation.waiting.component.WaitingBox
import com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiEvent
import com.survivalcoding.ai_court.presentation.waiting.viewmodel.WaitingViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitingScreen(
    roomCode: String,
    chatRoomId: Long,
    onNavigateToChat: (chatRoomId: Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WaitingViewModel = hiltViewModel()
) {
    val clipboardManager = LocalClipboardManager.current
    val scrollState =  rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(roomCode, chatRoomId) {
        viewModel.start(roomCode, chatRoomId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WaitingUiEvent.NavigateToChat -> onNavigateToChat(event.chatRoomId)
            }
        }
    }

    Column (
        modifier= modifier
            .fillMaxSize()
            .background(color= AI_COURTTheme.colors.cream)
            .verticalScroll(scrollState)
            .systemBarsPadding(),

        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ChatTopBar(roomCode= roomCode,
            onNavigateBack = onNavigateBack)
        Spacer(modifier= Modifier.height(68.dp))
        WaitingBox(roomCode= roomCode,
            onCopyRoomCode = {
            clipboardManager.setText(AnnotatedString(roomCode))
        }
            )
        Spacer(modifier= Modifier.height(25.dp))
        InfoBanner()

        Spacer(modifier= Modifier.height(112.dp))
    }

}

