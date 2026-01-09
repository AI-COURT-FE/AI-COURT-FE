package com.survivalcoding.ai_court.presentation.join.screen

import JoinTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.presentation.join.component.JoinInputBox
import com.survivalcoding.ai_court.presentation.join.component.ParticipantAlreadyExistsDialogComponent
import com.survivalcoding.ai_court.presentation.join.state.JoinUiEvent
import com.survivalcoding.ai_court.presentation.join.viewmodel.JoinViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JoinScreen(
    nickname: String,
    onJoinSuccess: (roomCode: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: JoinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var roomCode by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // 4) 성공 시 정상 진입
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is JoinUiEvent.NavigateToChat -> onJoinSuccess(event.roomCode)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AI_COURTTheme.colors.cream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AI_COURTTheme.colors.cream)
                .verticalScroll(scrollState)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JoinTopBar()
            Spacer(modifier = Modifier.height(70.dp))
            JoinInputBox(
                roomCode = roomCode,
                onRoomCodeChange = {
                    roomCode = it
                    localError = null
                }
            )
            // (빈 값) 로컬 에러
            localError?.let { err ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = err,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 3) 서버에서 내려준 “잘못된 방코드” 등 오류 메시지
            uiState.errorMessage?.let { err ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = err,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(105.dp))
            CourtButton(
                text = if (uiState.isLoading) "입장 중..." else "입장하기",
                onClick = {
                    val code = roomCode.trim()
                    if (code.isBlank()) {
                        localError = "방 코드를 입력해주세요."
                        return@CourtButton
                    }
                    viewModel.joinRoom(code, nickname)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.padding(horizontal = 32.dp),
                height = 100.dp,
                style = AI_COURTTheme.typography.Body_1
            )

            Spacer(modifier = Modifier.height(112.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AI_COURTTheme.colors.navy)
            }
        }
    }

    if (uiState.blockingMessage != null) {
        Dialog(
            onDismissRequest = { /* no-op */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            ParticipantAlreadyExistsDialogComponent(
//                text = uiState.blockingMessage!!
            )
        }
    }
}
