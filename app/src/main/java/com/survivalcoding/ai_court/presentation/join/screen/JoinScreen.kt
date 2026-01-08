package com.survivalcoding.ai_court.presentation.join.screen

import JoinTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.survivalcoding.ai_court.core.component.CourtButton
import com.survivalcoding.ai_court.presentation.entry.viewmodel.EntryViewModel
import com.survivalcoding.ai_court.presentation.join.component.JoinInputBox
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JoinScreen(
    onJoinSuccess: (roomCode: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
){
    var roomCode by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    Column (
        modifier= Modifier
            .fillMaxSize()
            .background(color = AI_COURTTheme.colors.cream)
            .verticalScroll(scrollState)
            .systemBarsPadding(),

        horizontalAlignment = Alignment.CenterHorizontally
    ){
        JoinTopBar()
        Spacer(modifier= Modifier.height(70.dp))
        JoinInputBox(
            roomCode = roomCode,
            onRoomCodeChange = { roomCode = it }
        )

        Spacer(modifier= Modifier.height(105.dp))
        CourtButton(
            text= "입장하기",
            onClick = {
                viewModel.joinRoom()
            },
        )

        Spacer(modifier= Modifier.height(112.dp))
    }
}
