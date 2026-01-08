package com.survivalcoding.ai_court.presentation.verdict.state

import com.survivalcoding.ai_court.domain.model.FinalVerdict

data class VerdictUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val finalVerdict: FinalVerdict? = null
)