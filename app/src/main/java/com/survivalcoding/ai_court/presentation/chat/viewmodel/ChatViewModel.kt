import androidx.lifecycle.ViewModel
import com.survivalcoding.ai_court.domain.model.ChatMessage
import com.survivalcoding.ai_court.presentation.chat.state.ChatUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputMessage = text) }
    }

    fun onSendClick(roomCode: String, myUserId: String) {
        val s = _uiState.value
        val text = s.inputMessage.trim()
        if (text.isBlank()) return

        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            roomCode = roomCode,
            senderId = myUserId,
            senderNickname = s.myNickname,
            content = text,
            timestamp = System.currentTimeMillis(),
            isMyMessage = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + newMessage,
                inputMessage = ""
            )
        }

        // TODO: 서버/Firebase 전송 붙일 자리
    }

    fun setMyNickname(nickname: String) {
        _uiState.update { it.copy(myNickname = nickname) }
    }

    fun setOpponentNickname(nickname: String) {
        _uiState.update { it.copy(opponentNickname = nickname) }
    }

    fun openVerdictDialog() {
        _uiState.update { it.copy(showVerdictDialog = true) }
    }

    fun closeVerdictDialog() {
        _uiState.update { it.copy(showVerdictDialog = false) }
    }

    fun onReceiveMessage(message: ChatMessage) {
        _uiState.update { it.copy(messages = it.messages + message) }
    }
}
