package com.thmanyah.shasha.presentation.state

import androidx.annotation.StringRes
import com.thmanyah.shasha.domain.model.Section

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val sections: List<Section>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(@StringRes val messageRes: Int) : SearchUiState
}
