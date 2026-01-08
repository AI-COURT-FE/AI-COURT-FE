package com.survivalcoding.ai_court.presentation.entry.screen

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.presentation.entry.component.LogoSection
import com.survivalcoding.ai_court.presentation.entry.component.NicknameInput
import com.survivalcoding.ai_court.presentation.entry.viewmodel.EntryViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun EntryScreen(
    onNavigateToWaiting: (roomCode: String, userId: String, nickname: String) -> Unit,
    onNavigateToJoin: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var joinError by rememberSaveable { mutableStateOf<String?>(null) }

    // 네비게이션 이벤트 처리
    LaunchedEffect(uiState.navigateToChat) {
        uiState.navigateToChat?.let { event ->
            onNavigateToWaiting(event.roomCode, event.userId, event.nickname)
            viewModel.onNavigationHandled()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(
                AI_COURTTheme.colors.cream
            )
            .padding(vertical = 30.dp, horizontal = 10.dp)
            .padding(bottom = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            // 로고/타이틀
            LogoSection()

            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "닉네임",
                style = AI_COURTTheme.typography.Body_1,
                color = AI_COURTTheme.colors.black
            )

            Spacer(Modifier.height(10.dp))

            NicknameInput(
                value = uiState.nickname,
                onValueChange = {
                    joinError = null
                    viewModel.onNicknameChanged(it)
                }
            )

            Spacer(Modifier.height(93.dp))

            // 방 만들기 버튼
            CourtButton(
                text = "채팅방 생성하기",
                onClick = viewModel::createRoom,
                enabled = !uiState.isLoading,
            )

            Spacer(modifier = Modifier.height(8.dp))

            CourtButton(
                text = "입장코드로 참여",
                onClick = {
                    if (uiState.nickname.isBlank()) {
                        joinError = "닉네임을 입력해주세요."
                    } else {
                        viewModel::createRoom
                        joinError = null
                        onNavigateToJoin()
                    }
                },
                enabled = !uiState.isLoading,
                containerColor = AI_COURTTheme.colors.redBrown
            )

            joinError?.let { error ->
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = error,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))
//
//            // TODO: 배포 전 삭제 - 디버그용 테스트 버튼
//            Spacer(modifier = Modifier.height(24.dp))
//            CourtButton(
//                text = "⚡ 테스트 입장 (DEBUG)",
//                onClick = { viewModel.debugEnterRoom() },
//                containerColor = Color(0xFFFF9800)
//            )
//            Spacer(modifier = Modifier.height(24.dp))

            // 에러 메시지
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(14.dp))
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
                    color = AI_COURTTheme.colors.navy
                )
            }

            Text(
                text = "본 서비스는 친구 사이의 사소한 논쟁을 해결하기\n위한 AI 판사입니다. 법적 효력은 없으며, 심각한\n갈등은 전문가와 상의하세요.",
                style = AI_COURTTheme.typography.Caption_regular,
                color = AI_COURTTheme.colors.gray500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}
