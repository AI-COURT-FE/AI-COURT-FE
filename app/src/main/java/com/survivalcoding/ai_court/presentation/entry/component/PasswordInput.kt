package com.survivalcoding.ai_court.presentation.entry.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.survivalcoding.ai_court.R
import com.survivalcoding.ai_court.ui.theme.AI_COURTTheme

@Composable
fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "비밀번호",
    height: Dp = 54.dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        placeholder = { Text(text = placeholder, color = AI_COURTTheme.colors.gray500, style = AI_COURTTheme.typography.Body_1) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = "자물쇠 아이콘",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        },
        textStyle = AI_COURTTheme.typography.Body_1,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AI_COURTTheme.colors.black,
            unfocusedBorderColor = AI_COURTTheme.colors.gray900,
            focusedContainerColor = AI_COURTTheme.colors.softWhite,
            unfocusedContainerColor = AI_COURTTheme.colors.white,
            focusedTextColor = AI_COURTTheme.colors.black,
            unfocusedTextColor = AI_COURTTheme.colors.black,
            cursorColor = AI_COURTTheme.colors.brown
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}

@Preview(showBackground = true)
@Composable
private fun NicknameInputPreview_Empty() {
    MaterialTheme {
        var text by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            NicknameInput(
                value = text,
                onValueChange = { text = it }
            )
        }
    }
}