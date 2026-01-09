package com.survivalcoding.ai_court.presentation.join.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun JoinInputBox(
    roomCode: String,
    onRoomCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(375.dp)
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
                .padding(top = 52.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "사건번호 입력",
                style = AI_COURTTheme.typography.Body_1,
                color = AI_COURTTheme.colors.black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "전달받은 사건번호를 입력하여 \n" +
                        "법정에 입장하세요",
                style = AI_COURTTheme.typography.Caption_regular,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = "자물쇠",
                modifier = Modifier
                    .size(65.dp)
            )

            Spacer(modifier = Modifier.height(23.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .height(122.dp)
                    .border(1.dp, Color(0xFFBABABA), innerShape)
                    .background(Color(0xFFEDEDED), innerShape)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "사건번호를 입력하세요.",
                        style = AI_COURTTheme.typography.Caption_regular,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = roomCode, // roomCode는 하이픈 없는 "원본"이라고 가정 (예: "12345")
                            onValueChange = { raw ->
                                val digits = raw.filter { it.isDigit() }.take(8)
                                onRoomCodeChange(digits)
                            },
                            visualTransformation = RoomCodeVisualTransformation(), // ✅ 화면에만 ####-#### 표시 + 커서 보정
                            singleLine = true,
                            textStyle = AI_COURTTheme.typography.Title_1.copy(
                                color = AI_COURTTheme.colors.black,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (roomCode.isBlank()) {
                                    Text(
                                        text = "XXXX-XXXX",
                                        style = AI_COURTTheme.typography.Title_1,
                                        color = Color(0xFF8C8C8C),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                innerTextField()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Done
                            )
                        )

                    }
                }
            }
        }
    }
}

private class RoomCodeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text

        val transformed = buildString {
            if (original.length < 4) {
                append(original)
            } else {
                // 4자리 되는 순간 바로 하이픈 표시 (1234-)
                append(original.substring(0, 4))
                append('-')
                if (original.length > 4) {
                    append(original.substring(4))
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // 4자리 이상이면 하이픈 1칸 추가되므로 커서도 1칸 뒤로
                return if (offset < 4) offset else offset + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                // transformed에서 '-'(index 4) 뒤(>=5)는 원본에서는 -1
                return if (offset <= 4) offset else offset - 1
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

@Preview(showBackground = true)
@Composable
private fun WaitingBoxPrev() {
    AI_COURTTheme {
        var code by rememberSaveable { mutableStateOf("") }
        JoinInputBox(
            roomCode = code,
            onRoomCodeChange = { code = it }
        )
    }
}
