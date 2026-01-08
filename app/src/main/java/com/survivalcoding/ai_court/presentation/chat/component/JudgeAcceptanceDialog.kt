package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JudgeAcceptable(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000),
                    shape = RoundedCornerShape(27.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF755139),
                    shape = RoundedCornerShape(27.dp)
                )
                .width(337.dp)
                .height(302.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(27.dp)
                )
                .clip(RoundedCornerShape(27.dp))
                .padding(24.dp),
        ) {
            JudgeAcceptableContent(
                onCancel = onCancel,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun JudgeAcceptableContent(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_judge),
            contentDescription = null,
            modifier = Modifier
                .width(127.dp)
                .height(97.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "상대방이 판결을 요청했습니다.\n지금까지의 대화를 바탕으로\n판결을 수락하시겠습니까?",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JudgeAcceptableButton(
                text = "취소",
                background = AI_COURTTheme.colors.gray400,
                textColor = Color.Black,
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )

            JudgeAcceptableButton(
                text = "확인",
                background = Color(0xFF7B5A3C),
                textColor = Color.White,
                onClick = onConfirm,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun JudgeAcceptableButton(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEFE7DA)
@Composable
private fun JudgeAcceptablePreview() {
    Box(
        modifier = Modifier
            .width(337.dp)
            .height(302.dp)
            .background(Color.White, RoundedCornerShape(27.dp))
            .border(1.dp, Color(0xFF755139), RoundedCornerShape(27.dp))
            .clip(RoundedCornerShape(27.dp))
            .padding(24.dp)
    ) {
        JudgeAcceptableContent(
            onCancel = {},
            onConfirm = {}
        )
    }
}
