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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.survivalcoding.ai_court.presentation.waiting.viewmodel.WaitingViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WaitingScreen(
    inviteCode: String,                 // 화면 표시/복사할 코드
    chatRoomId: Long,                   // pollChatRoom에 넣을 ID
    onNavigateToChat: (chatRoomId: Long, inviteCode: String) -> Unit,
    viewModel: WaitingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    // 폴링 시작
    LaunchedEffect(inviteCode, chatRoomId) {
        viewModel.startPolling(inviteCode = inviteCode, chatRoomId = chatRoomId)
    }

    // 네비게이션 이벤트 수신
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is com.survivalcoding.ai_court.presentation.waiting.state.WaitingUiEvent.NavigateToChat -> {
                    onNavigateToChat(event.chatRoomId, event.inviteCode)
                }
            }
        }
    }

    // 화면 떠날 때 폴링 종료
    DisposableEffect(Unit) {
        onDispose { viewModel.stopPolling() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단바: 초대코드 표시(기존 컴포넌트 그대로 사용)
        ChatTopBar(roomCode = inviteCode, onNavigateBack = { /* 필요하면 추가 */ })

        Spacer(modifier = Modifier.height(24.dp))

        // 초대코드 박스: 복사도 초대코드 기준
        WaitingBox(
            roomCode = inviteCode,
            onCopyRoomCode = {
                clipboardManager.setText(AnnotatedString(inviteCode))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 참가자 수 표시를 하고 싶으면 여기서 사용 가능
        // 예: Text("참여자: ${uiState.participantCount}/2")

        Spacer(modifier = Modifier.height(25.dp))

        InfoBanner()

        Spacer(modifier = Modifier.height(112.dp))
    }
}