package com.survivalcoding.ai_court.presentation.verdict.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.survivalcoding.ai_court.presentation.verdict.viewmodel.VerdictViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerdictScreen(
    roomCode: String,
    onNavigateBack: () -> Unit,
    onGoToMain: () -> Unit,
    viewModel: VerdictViewModel = hiltViewModel()
) {
}