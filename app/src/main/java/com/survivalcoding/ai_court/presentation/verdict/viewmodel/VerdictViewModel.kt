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
     * 최종 판결문 1회 요청용
     * 실시간 verdict는 여기서 다루지 않음
     */
    fun loadFinalVerdict(roomCode: String, leftName: String, rightName: String) {
        if (roomCode.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "roomCode가 비어있습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val res = finalVerdictRepository.requestFinalVerdict(roomCode)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            finalVerdict = res.data,
                            errorMessage = null
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = res.message ?: "최종 판결 요청 실패"
                        )
                    }
                }

                is Resource.Loading -> {
                    // Loading 타입이 있는 Resource면 여기로 들어올 수 있음
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearVerdict() {
        _uiState.update { it.copy(finalVerdict = null) }
    }
}