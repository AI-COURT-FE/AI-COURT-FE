package com.survivalcoding.ai_court.presentation.entry.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.presentation.entry.EntryViewModel
import com.survivalcoding.ai_court.presentation.entry.component.NicknameInput
import com.survivalcoding.ai_court.presentation.entry.component.RoomCodeInput
import com.survivalcoding.ai_court.presentation.entry.component.WaitingDialog

@Composable
fun EntryScreen(
    onNavigateToChat: (roomCode: String, userId: String, nickname: String) -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 네비게이션 이벤트 처리
    LaunchedEffect(uiState.navigateToChat) {
        uiState.navigateToChat?.let { event ->
            onNavigateToChat(event.roomCode, event.userId, event.nickname)
            viewModel.onNavigationHandled()
        }
    }

    // 대기 다이얼로그
    if (uiState.isWaitingForOpponent && uiState.createdRoomCode != null) {
        WaitingDialog(
            roomCode = uiState.createdRoomCode!!,
            onDismiss = { viewModel.cancelWaiting() }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1A),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // 로고/타이틀
            Text(
                text = "⚖️",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "이의있오",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "AI가 판결하는 실시간 논쟁 배틀",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // 닉네임 입력
            Text(
                text = "닉네임",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            NicknameInput(
                value = uiState.nickname,
                onValueChange = viewModel::onNicknameChanged
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 방 만들기 버튼
            CourtButton(
                text = "방 만들기",
                onClick = viewModel::createRoom,
                enabled = !uiState.isLoading,
                containerColor = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 구분선
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF3D3D5C)
                )
                Text(
                    text = "또는",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF3D3D5C)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 방 코드 입력
            Text(
                text = "방 코드",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            RoomCodeInput(
                value = uiState.roomCode,
                onValueChange = viewModel::onRoomCodeChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 입장하기 버튼
            CourtButton(
                text = "입장하기",
                onClick = viewModel::joinRoom,
                enabled = !uiState.isLoading,
                containerColor = Color(0xFFFF6B6B)
            )

            // 에러 메시지
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 로딩 인디케이터
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = Color(0xFF4ECDC4)
                )
            }
        }
    }
}

