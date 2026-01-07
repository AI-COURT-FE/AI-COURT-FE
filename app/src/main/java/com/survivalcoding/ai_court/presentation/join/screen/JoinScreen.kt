package com.survivalcoding.ai_court.presentation.join.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.survivalcoding.ai_court.presentation.entry.viewmodel.EntryViewModel

@Composable
fun JoinScreen(
    onJoinSuccess: (roomCode: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
){

}