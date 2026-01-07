package com.survivalcoding.ai_court.core.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun CourtButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    containerColor: Color = AI_COURTTheme.colors.brown,
    contentColor: Color = AI_COURTTheme.colors.cream,
    showShadow: Boolean = true,
    showIcon: Boolean = false,
) {
    val shape = RoundedCornerShape(16.dp)

    Box(modifier = modifier.fillMaxWidth()) {

        if (showShadow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = shape,
                        clip = false,
                        ambientColor = AI_COURTTheme.colors.black,
                        spotColor = Color.Black,
                    )
                    .background(Color.Transparent)
            )
        }

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (showIcon) {
                    Icon(
                        painter = painterResource(id= R.drawable.ic_baton),
                        contentDescription = "법봉",
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier= Modifier.padding(5.dp))
                }
                Text(
                    text = text,
                    style = AI_COURTTheme.typography.Body_2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourtButtonPreview_Default() {
    MaterialTheme {
        CourtButton(
            text = "판결 요청",
            onClick = {},
            modifier = Modifier.padding(16.dp),
            showIcon = true
        )
    }
}