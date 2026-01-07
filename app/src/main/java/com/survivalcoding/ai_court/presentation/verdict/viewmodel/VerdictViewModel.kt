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

    /**
     * ✅ "최종 판결문" 1회 요청용
     * - 실시간 verdict는 여기서 다루지 않음
     */
    fun requestFinalVerdict(roomCode: String) {
        if (roomCode.isBlank()) {
            _uiState.update { it.copy(errorMessage = "방 코드가 올바르지 않습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = finalVerdictRepository.requestFinalVerdict(roomCode)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            finalVerdict = result.data,
                            errorMessage = null
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "최종 판결 요청에 실패했습니다."
                        )
                    }
                }

                is Resource.Loading -> {
                    // isLoading으로 처리
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}