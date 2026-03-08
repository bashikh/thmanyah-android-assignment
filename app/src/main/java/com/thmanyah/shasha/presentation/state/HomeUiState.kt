package com.thmanyah.shasha.presentation.state

import androidx.annotation.StringRes
import com.thmanyah.shasha.domain.model.Section

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val sections: List<Section>,
        val isLoadingMore: Boolean = false,
    ) : HomeUiState
    data class Error(@StringRes val messageRes: Int) : HomeUiState
    data object Empty : HomeUiState
}
