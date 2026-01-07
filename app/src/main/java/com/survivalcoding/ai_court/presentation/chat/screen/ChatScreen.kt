package com.survivalcoding.ai_court.presentation.chat.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.domain.model.WinRate
import com.survivalcoding.ai_court.presentation.chat.component.ChatBubble
import com.survivalcoding.ai_court.presentation.chat.component.ChatInput
import com.survivalcoding.ai_court.presentation.chat.component.VerdictDialog
import com.survivalcoding.ai_court.presentation.chat.component.WinRateHeader
import com.survivalcoding.ai_court.presentation.chat.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomCode: String,
    userId: String,
    nickname: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // 채팅방 연결
    LaunchedEffect(roomCode) {
        viewModel.connectToRoom(roomCode, userId, nickname)
    }

    // 새 메시지가 오면 스크롤
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // 판결 다이얼로그
    if (uiState.showVerdictDialog && uiState.verdict != null) {
        VerdictDialog(
            verdict = uiState.verdict!!,
            onDismiss = { viewModel.dismissVerdictDialog() },
            onGoToMain = {
                viewModel.dismissVerdictDialog()
                onNavigateBack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "⚖️ 이의있오",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "방 코드: $roomCode",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.requestVerdict() },
                containerColor = Color(0xFFFF6B6B),
                contentColor = Color.White
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    Icon(
                        painterResource(R.drawable.ic_gavel),
                        contentDescription = "판결 요청"
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0F1A),
                            Color(0xFF1A1A2E)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                // 승률 게이지
                WinRateHeader(
                    myNickname = uiState.myNickname.ifEmpty { "나" },
                    opponentNickname = uiState.opponentNickname,
                    winRate = uiState.winRate
                )

                // 채팅 메시지 리스트
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState
                ) {
                    if (uiState.messages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "대화를 시작해보세요!\nAI가 실시간으로 승률을 분석합니다 🎯",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(message = message)
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                // 메시지 입력창
                ChatInput(
                    value = uiState.inputMessage,
                    onValueChange = viewModel::onInputMessageChanged,
                    onSend = viewModel::sendMessage
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChatInputPreview() {
    ChatInput(value = "", onValueChange = {}, onSend = {})
}

@Preview
@Composable
private fun WinRateHeaderPreview() {
    WinRateHeader(
        myNickname = "나",
        opponentNickname = "상대방",
        winRate = WinRate(50, 50)
    )
}
