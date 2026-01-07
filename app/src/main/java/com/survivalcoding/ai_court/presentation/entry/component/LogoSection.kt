package com.survivalcoding.ai_court.presentation.entry.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun LogoSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, AI_COURTTheme.colors.gray400),
            colors = CardDefaults.cardColors(
                containerColor = AI_COURTTheme.colors.white,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "앱 로고",
                tint = Color.Unspecified,
                modifier = Modifier.size(150.dp)
            )
        }

        Spacer(Modifier.height(7.dp))

        Text(
            text = "AI 재판장",
            style = AI_COURTTheme.typography.Caption_3,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LogoSectionPrev() {
    LogoSection()
}