package com.survivalcoding.ai_court.presentation.join.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun ParticipantAlreadyExistsDialogComponent(
    modifier: Modifier = Modifier,
    text: String = "이미 피고인이 있습니다"
){
    Box(
        modifier = Modifier.size(width = 325.dp, height = 380.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AI_COURTTheme.colors.white),
        contentAlignment = Alignment.Center,
    ){
        Text(
        text = text,
            style = AI_COURTTheme.typography.Body_1)
    }
}

@Preview(showBackground = false)
@Composable
fun ParticipantAlreadyExistsDialogComponentPrev(){
    ParticipantAlreadyExistsDialogComponent(modifier = Modifier)
}