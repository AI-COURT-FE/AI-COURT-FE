package com.survivalcoding.ai_court.presentation.join.screen

import JoinTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.survivalcoding.ai_court.presentation.entry.viewmodel.EntryViewModel
import com.survivalcoding.ai_court.presentation.join.component.JoinInputBox
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JoinScreen(
    onJoinSuccess: (roomCode: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EntryViewModel = hiltViewModel()
){
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
        JoinInputBox()

        Spacer(modifier= Modifier.height(105.dp))
        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(83.dp)
                .background(color = Color(0xFF755139), shape = RoundedCornerShape(size = 16.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFF8C8C8C),
                    shape = RoundedCornerShape(size = 16.dp),
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center){
            Text(
                "입장하기",
            style=AI_COURTTheme.typography.Body_1
            )

        }

        Spacer(modifier= Modifier.height(112.dp))
    }
}
