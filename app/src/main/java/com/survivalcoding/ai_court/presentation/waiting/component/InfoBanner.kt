package com.survivalcoding.ai_court.presentation.waiting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun InfoBanner(modifier: Modifier = Modifier) {
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)
        .height(83.dp)
        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(size = 16.dp))
        .border(width = 1.dp, color = Color(0xFF8C8C8C), shape = RoundedCornerShape(size = 16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            painterResource(R.drawable.ic_info),
            contentDescription = "안내",
            modifier= Modifier.padding(start=13.dp)
        )
        Text("두명 모두 입장하면 자동으로 \n" +
                "재판이 시작됩니다.",
            style = AI_COURTTheme.typography.Caption_regular,
            modifier= Modifier.padding(start=10.dp)
        )
    }
}

@Preview
@Composable
private fun InfoBannerPrev() {
    InfoBanner()
}