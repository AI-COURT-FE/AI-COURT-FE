package com.survivalcoding.ai_court.presentation.verdict.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.ai_court.core.util.Resource
import com.survivalcoding.ai_court.domain.repository.FinalVerdictRepository
import com.survivalcoding.ai_court.presentation.verdict.state.VerdictUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerdictViewModel @Inject constructor(
    private val finalVerdictRepository: FinalVerdictRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerdictUiState())
    val uiState: StateFlow<VerdictUiState> = _uiState.asStateFlow()

    fun loadFinalVerdict(chatRoomId: Long) {
        if (chatRoomId <= 0L) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "chatRoomId가 올바르지 않습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val res = finalVerdictRepository.getFinalJudgement(chatRoomId)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, finalVerdict = res.data, errorMessage = null)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = res.message ?: "최종 판결 조회 실패")
                }
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}