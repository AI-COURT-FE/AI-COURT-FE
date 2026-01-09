package com.survivalcoding.ai_court.presentation.chat.screen

import ChatBubble
import ChatInput
import ChatTopBar
import WinRateHeader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.domain.model.ChatRoomStatus
import com.survivalcoding.ai_court.presentation.chat.component.JudgeAcceptanceDialog
import com.survivalcoding.ai_court.presentation.chat.component.UntilDoneComponent
import com.survivalcoding.ai_court.presentation.chat.state.ChatUiState
import com.survivalcoding.ai_court.presentation.chat.viewmodel.ChatViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme
import java.util.UUID

@Composable
private fun ChatScreenContent(
    roomCode: String,
    uiState: ChatUiState,
    onNavigateBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onRequestVerdict: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(color = AI_COURTTheme.colors.cream)
    ) {
        ChatTopBar(roomCode = roomCode, onNavigateBack = onNavigateBack)

        WinRateHeader(
            leftName = uiState.opponentNickname,
            rightName = uiState.myNickname,
            leftScore = uiState.winRate.userAScore,
            rightScore = uiState.winRate.userBScore,
            onRequestVerdict = onRequestVerdict
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(
                    top = 12.dp + 6.dp + 26.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(items = uiState.messages, key = { it.id }) { message ->
                    ChatBubble(
                        message = message,
                        isMine = message.isMyMessage
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .height(26.dp)
                    .background(
                        color = Color(0xFF333333),
                        shape = RoundedCornerShape(13.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI 판사가 실시간 분석 중입니다.",
                    style = AI_COURTTheme.typography.Caption_3,
                    color = AI_COURTTheme.colors.white,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        val bottomInsets = WindowInsets.ime.union(WindowInsets.navigationBars)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(bottomInsets)
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
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
                    .padding(1.dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF292D47))
                    .clickable(enabled = uiState.inputMessage.isNotBlank()) { onSendClick() },
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
}

@Composable
fun ChatScreen(
    roomCode: String,
    myUserId: String,
    myNickname: String = "나",
    opponentNickname: String = "상대방",
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToVerdict: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val fallbackUserId = rememberSaveable { UUID.randomUUID().toString() }
    val userIdToUse = if (myUserId.isBlank()) fallbackUserId else myUserId

    // ✅ 화면 진입 시 한 번만 폴링 연결
    LaunchedEffect(roomCode, userIdToUse, myNickname, opponentNickname) {
        if (roomCode.isNotBlank()) {
            viewModel.initialize(roomCode, userIdToUse, myNickname, opponentNickname)
        }
    }

    // ✅ DONE -> VerdictScreen (1회만)
    val hasNavigated = rememberSaveable(roomCode) { mutableStateOf(false) }
    LaunchedEffect(uiState.chatRoomStatus) {
        if (uiState.chatRoomStatus == ChatRoomStatus.DONE && !hasNavigated.value) {
            hasNavigated.value = true
            onNavigateToVerdict(roomCode.toChatRoomIdOrZero())
        }
    }

    Box(Modifier.fillMaxSize()) {
        ChatScreenContent(
            roomCode = roomCode,
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onInputChange = viewModel::onInputChange,
            onSendClick = viewModel::onSendClick,
            onRequestVerdict = {
                // "판결 요청" = 종료 요청 보내기
                viewModel.requestExit()
            }
        )

        // ✅ REQUEST_ACCEPT -> UntilDoneComponent (터치 차단 오버레이)
        if (uiState.chatRoomStatus == ChatRoomStatus.REQUEST_ACCEPT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                UntilDoneComponent(Modifier.align(Alignment.Center))
            }
        }

        // ✅ REQUEST_FINISH + 상대방이 요청자면 승인/거절 다이얼로그
        if (uiState.showFinishApprovalDialog) {
            JudgeAcceptanceDialog(
                onCancel = { viewModel.rejectExit() },
                onConfirm = { viewModel.approveExit() }
            )
        }
    }
}

private fun String.toChatRoomIdOrZero(): Long =
    this.replace("-", "").toLongOrNull() ?: 0L
