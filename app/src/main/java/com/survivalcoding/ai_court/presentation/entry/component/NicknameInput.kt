package com.survivalcoding.ai_court.presentation.entry.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.ui.theme.AICourtColors
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun NicknameInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "닉네임을 입력하세요"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = AI_COURTTheme.colors.gray500
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4ECDC4),
            unfocusedBorderColor = Color(0xFF3D3D5C),
            focusedContainerColor = Color(0xFF2D2D44),
            unfocusedContainerColor = Color(0xFF2D2D44),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF4ECDC4)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NicknameInputPreview_Empty() {
    MaterialTheme {
        var text by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E))
                .padding(16.dp)
        ) {
            NicknameInput(
                value = text,
                onValueChange = { text = it }
            )
        }
    }
}