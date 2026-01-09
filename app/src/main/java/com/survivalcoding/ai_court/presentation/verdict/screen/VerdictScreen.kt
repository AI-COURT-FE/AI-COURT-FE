package com.survivalcoding.ai_court.presentation.verdict.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.presentation.verdict.component.FinalGaugeBar
import com.survivalcoding.ai_court.presentation.verdict.component.JudgmentComponent
import com.survivalcoding.ai_court.presentation.verdict.state.VerdictUiState
import com.survivalcoding.ai_court.presentation.verdict.viewmodel.VerdictViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerdictScreen(
    chatRoomId: Long,
    onNavigateBack: () -> Unit,
    onShareVerdict: () -> Unit,
    onGoEntry: () -> Unit,
    viewModel: VerdictViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(chatRoomId) {
        viewModel.loadFinalVerdict(chatRoomId)
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("확인") }
            },
            title = { Text("오류") },
            text = { Text(uiState.errorMessage ?: "") }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "최종 판결문",
                            style = AI_COURTTheme.typography.Body_1,
                            color = AI_COURTTheme.colors.white
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AI_COURTTheme.colors.navy
                    )
                )
            },
            containerColor = AI_COURTTheme.colors.cream
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(AI_COURTTheme.colors.cream)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(bottomEnd = 103.dp, bottomStart = 103.dp))
                        .background(AI_COURTTheme.colors.navy)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }

                        uiState.finalVerdict != null -> {
                            val v = uiState.finalVerdict!!

                            JudgmentComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                winnerNickname = v.winnerNickname,
                                plaintiffNickname = v.plaintiffNickname,
                                defendantNickname = v.defendantNickname,
                                judgmentComment = v.judgmentComment,
                                winnerReason = v.winnerReason,
                                loserReason = v.loserReason,
                                gauges = {
                                    FinalGaugeBar(
                                        title = "논리력",
                                        leftName = v.plaintiffNickname,
                                        rightName = v.defendantNickname,
                                        leftScore = v.plaintiffLogicScore,
                                        rightScore = v.defendantLogicScore,
                                    )
                                    FinalGaugeBar(
                                        title = "공감력",
                                        leftName = v.plaintiffNickname,
                                        rightName = v.defendantNickname,
                                        leftScore = v.plaintiffEmpathyScore,
                                        rightScore = v.defendantEmpathyScore,
                                    )
                                }
                            )
                        }

                        else -> {
                            // ✅ DONE 전이면 여기로 올 수 있음(404 등) → 재시도 UX
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "판결문이 아직 준비되지 않았어요.",
                                        color = AI_COURTTheme.colors.white
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    CourtButton(
                                        text = "다시 불러오기",
                                        onClick = { viewModel.loadFinalVerdict(chatRoomId) }
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CourtButton(
                            text = "판결문 공유하기",
                            onClick = onShareVerdict,
                            modifier = Modifier.fillMaxWidth()
                        )
                        CourtButton(
                            text = "처음으로 돌아가기",
                            onClick = onGoEntry,
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color.Transparent,
                            contentColor = AI_COURTTheme.colors.gray400,
                            showShadow = false
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_stamp),
            contentDescription = "도장",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 35.dp)
                .offset(x = 20.dp, y = (-20).dp)
                .size(150.dp)
                .zIndex(10f)
        )
    }
}