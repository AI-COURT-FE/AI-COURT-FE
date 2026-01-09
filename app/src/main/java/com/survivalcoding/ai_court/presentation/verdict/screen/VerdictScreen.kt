package com.survivalcoding.ai_court.presentation.verdict.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import com.survivalcoding.ai_court.domain.model.FinalVerdict
import com.survivalcoding.ai_court.presentation.verdict.component.FinalGaugeBar
import com.survivalcoding.ai_court.presentation.verdict.component.JudgmentComponent
import com.survivalcoding.ai_court.presentation.verdict.state.VerdictUiState
import com.survivalcoding.ai_court.presentation.verdict.viewmodel.VerdictViewModel
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerdictScreen(
    roomCode: String,
    leftName: String,
    rightName: String,
    onNavigateBack: () -> Unit,
    onShareVerdict: () -> Unit,
    onGoEntry: () -> Unit,
    viewModel: VerdictViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(roomCode) {
        if (roomCode.isNotBlank()) {
            viewModel.loadFinalVerdict(roomCode, leftName, rightName)
        }
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("확인")
                }
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
                    navigationIcon = { },
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
                            val loser = if (v.winnerNickname == leftName) rightName else leftName

                            JudgmentComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                winnerNickname = v.winnerNickname,
                                loserNickname = loser,
                                reason = v.reason,
                                summary = v.summary,
                                gauges = {
                                    FinalGaugeBar(
                                        title = "논리력",
                                        leftName = leftName,
                                        rightName = rightName,
                                        leftScore = v.logicA,
                                        rightScore = v.logicB,
                                    )
                                    FinalGaugeBar(
                                        title = "공감력",
                                        leftName = leftName,
                                        rightName = rightName,
                                        leftScore = v.empathyA,
                                        rightScore = v.empathyB,
                                    )
                                }
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "판결 데이터를 불러오지 못했어요.",
                                    color = AI_COURTTheme.colors.white
                                )
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

// // // // // // // // //
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerdictScreenPreviewContent(
    uiState: VerdictUiState,
    leftName: String,
    rightName: String,
    onShareVerdict: () -> Unit = {},
    onGoEntry: () -> Unit = {},
) {
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
                    navigationIcon = { },
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
                            val v = uiState.finalVerdict
                            val loser = if (v.winnerNickname == leftName) rightName else leftName

                            JudgmentComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                winnerNickname = v.winnerNickname,
                                loserNickname = loser,
                                reason = v.reason,
                                summary = v.summary,
                                gauges = {
                                    FinalGaugeBar(
                                        title = "논리력",
                                        leftName = leftName,
                                        rightName = rightName,
                                        leftScore = v.logicA,
                                        rightScore = v.logicB,
                                    )
                                    FinalGaugeBar(
                                        title = "공감력",
                                        leftName = leftName,
                                        rightName = rightName,
                                        leftScore = v.empathyA,
                                        rightScore = v.empathyB,
                                    )
                                }
                            )
                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "판결 데이터를 불러오지 못했어요.",
                                    color = AI_COURTTheme.colors.white
                                )
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


@Preview(showBackground = true) // 크림 느낌(대충)
@Composable
private fun VerdictScreenPreview_Loading() {
    AI_COURTTheme {
        VerdictScreenPreviewContent(
            uiState = VerdictUiState(
                isLoading = true,
                finalVerdict = null,
                errorMessage = null
            ),
            leftName = "김논리",
            rightName = "박감성"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VerdictScreenPreview_Success() {
    AI_COURTTheme {
        VerdictScreenPreviewContent(
            uiState = VerdictUiState(
                isLoading = false,
                finalVerdict = FinalVerdict(
                    winnerNickname = "ㅇㄴㄹㅁㄴㅇㅁㄹㄴㅇㅁㄹㄴㅇ김논리",
                    reason = "상대의 주장에 반박 근거가 명확했고, 주장 간 모순이 적었습니다.",
                    summary = listOf("이번 논쟁은 핵심 쟁점 2개 중 2개 모두에서 김논리님의 주장이 설득력이 높았습니다.", "냐냐냐냔"),
                    logicA = 78,
                    logicB = 42,
                    empathyA = 55,
                    empathyB = 61
                ),
                errorMessage = null
            ),
            leftName = "김논리",
            rightName = "박감성"
        )
    }
}

