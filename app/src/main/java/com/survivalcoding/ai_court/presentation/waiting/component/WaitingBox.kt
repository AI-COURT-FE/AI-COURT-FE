package com.survivalcoding.ai_court.presentation.waiting.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun WaitingBox(modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(452.dp)
            .clip(cardShape)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(
                    color = AI_COURTTheme.colors.darkNavy,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                )
        )

        Column(
            modifier = Modifier
                .padding(top = 53.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_waiting_room),
                contentDescription = "대기 아이콘",
                modifier = Modifier.size(69.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "피고 소환 대기 중",
                style = AI_COURTTheme.typography.Body_1,
                color = AI_COURTTheme.colors.black
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "상대방에게 사건번호 또는 링크를\n전송하여 법정에 입장시키세요.",
                style = AI_COURTTheme.typography.Caption_regular
            )

            Spacer(modifier = Modifier.height(21.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(122.dp)
                    .border(1.dp, Color(0xFFBABABA), innerShape)
                    .background(Color(0xFFEDEDED), innerShape)
                    .padding(horizontal = 19.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "사건번호",
                        style = AI_COURTTheme.typography.Caption_regular,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        text = "2024-1234",
                        style = AI_COURTTheme.typography.Title_1,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = "복사하기",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(54.dp)
                    .border(1.dp, AI_COURTTheme.colors.black, innerShape)
                    .background(AI_COURTTheme.colors.white, innerShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "공유하기",
                    modifier = Modifier
                        .padding(start = 21.dp)
                        .size(24.dp)
                )

                Text(
                    text = "초대링크 복사하기",
                    style = AI_COURTTheme.typography.Body_2,
                    modifier = Modifier.padding(start = 38.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WaitingBoxPrev() {
    AI_COURTTheme {
        WaitingBox()
    }
}
