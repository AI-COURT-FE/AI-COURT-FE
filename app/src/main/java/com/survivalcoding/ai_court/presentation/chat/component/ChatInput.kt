package com.survivalcoding.ai_court.presentation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A2E))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            placeholder = {
                Text(
                    text = "메시지를 입력하세요",
                    color = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4ECDC4),
                unfocusedBorderColor = Color(0xFF3D3D5C),
                focusedContainerColor = Color(0xFF2D2D44),
                unfocusedContainerColor = Color(0xFF2D2D44),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF4ECDC4)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )

        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (value.isNotBlank()) Color(0xFF4ECDC4) else Color(0xFF3D3D5C),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "전송",
                tint = if (value.isNotBlank()) Color(0xFF0F0F1A) else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatInputPrev(){
    var text by remember { mutableStateOf("") }

    ChatInput(
        value = text,
        onValueChange = { text = it },
        onSend = { /* preview에서는 동작만 비워둠 */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

/*
value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
 */