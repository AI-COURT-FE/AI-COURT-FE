package com.survivalcoding.ai_court.presentation.chat.screen

import ChatBubble
import ChatInput
import ChatTopBar
import WinRateHeader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.presentation.chat.component.JudgeConfirmDialog
import com.survivalcoding.ai_court.presentation.chat.state.ChatUiState
import com.survivalcoding.ai_court.presentation.chat.viewmodel.ChatViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
private fun ChatScreenContent(
    roomCode: String,
    uiState: ChatUiState,
    onNavigateBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onCancelVerdict: () -> Unit,
    onConfirmVerdict: () -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), // 상태바만
        topBar = {
            Column {
                ChatTopBar(
                    roomCode = roomCode,
                    onNavigateBack = onNavigateBack
                )
                WinRateHeader(
                    leftName = uiState.opponentNickname,
                    rightName = uiState.myNickname,
                    leftScore = uiState.winRate.userAScore,
                    rightScore = uiState.winRate.userBScore
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AI_COURTTheme.colors.cream)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            ) {
                ChatInput(
                    value = uiState.inputMessage,
                    onValueChange = onInputChange,
                    onSendClick = onSendClick,
                    modifier = Modifier
                        .padding(end = 17.dp)
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF292D47)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AI_COURTTheme.colors.cream)
        ) {

            // 🔹 AI 분석중 배너
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .height(26.dp)
                        .background(
                            color = Color(0xFF333333),
                            shape = RoundedCornerShape(13.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "AI 판사가 실시간 분석 중입니다.",
                        style = AI_COURTTheme.typography.Caption_3,
                        color = AI_COURTTheme.colors.white,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // 🔹 메시지 리스트 (스크롤 영역)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(
                    items = uiState.messages,
                    key = { it.id }
                ) { message ->
                    ChatBubble(
                        message = message,
                        isMine = message.isMyMessage
                    )
                }
            }
        }

        // 🔹 판결 다이얼로그
        if (uiState.showVerdictDialog) {
            JudgeConfirmDialog(
                onCancel = onCancelVerdict,
                onConfirm = onConfirmVerdict
            )
        }
    }
}


@Composable
fun ChatScreen(
    roomCode: String,
    myUserId: String,
    myNickname: String = "나",
    opponentNickname: String = "상대방",
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 화면 진입 시 한 번만 초기화
    LaunchedEffect(roomCode, myUserId) {
        if (roomCode.isNotBlank() && myUserId.isNotBlank()) {
            viewModel.initialize(roomCode, myUserId, myNickname, opponentNickname)
        }
    }

    ChatScreenContent(
        roomCode = roomCode,
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onInputChange = viewModel::onInputChange,
        onSendClick = viewModel::onSendClick,
        onCancelVerdict = viewModel::closeVerdictDialog,
        onConfirmVerdict = {
            viewModel.closeVerdictDialog()
            // 판결 요청 로직
        }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun ChatScreenPreview() {
    val fakeMessages = listOf(
        ChatMessage(
            id = "1",
            roomCode = "TEST123",
            senderId = "opponent",
            senderNickname = "박논리",
            content = "솔직히 네가 늦은 건 맞잖아\n사과는 해야지",
            timestamp = System.currentTimeMillis(),
            isMyMessage = false
        ), ChatMessage(
            id = "2",
            roomCode = "TEST123",
            senderId = "me",
            senderNickname = "김논리",
            content = "아니 차가 막힌 걸 어쩔 수 없잖아",
            timestamp = System.currentTimeMillis(),
            isMyMessage = true
        ), ChatMessage(
            id = "3",
            roomCode = "TEST123",
            senderId = "me",
            senderNickname = "김논리",
            content = "아니 차가 막힌 걸 어쩔 수 없잖아",
            timestamp = System.currentTimeMillis(),
            isMyMessage = true
        )
    )

    val fakeUiState = ChatUiState(
        messages = fakeMessages,
        inputMessage = "프리뷰 입력중…",
        showVerdictDialog = false,
        myNickname = "김논리",
        opponentNickname = "박논리"
    )

    ChatScreenContent(
        roomCode = "TEST123",
        uiState = fakeUiState,
        onNavigateBack = {},
        onInputChange = {},
        onSendClick = {},
        onCancelVerdict = {},
        onConfirmVerdict = {}
    )
}
