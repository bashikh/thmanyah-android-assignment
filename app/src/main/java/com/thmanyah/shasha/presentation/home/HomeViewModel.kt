package com.thmanyah.shasha.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.core.util.toErrorStringRes
import com.thmanyah.shasha.domain.usecase.GetHomeSectionsUseCase
import com.thmanyah.shasha.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase,
) : ViewModel() {

    private companion object {
        val PAGE_QUERY_REGEX = Regex("[?&]page=(\\d+)")
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var totalPages: Int? = null
    private var nextPage: String? = null
    private var allSections = mutableListOf<Section>()
    private var isLoadingMore = false

    init {
        loadHomeSections()
    }

    fun loadHomeSections() {
        viewModelScope.launch {
            currentPage = 1
            allSections.clear()
            _uiState.value = HomeUiState.Loading

            getHomeSectionsUseCase(currentPage).fold(
                onSuccess = { result ->
                    allSections.addAll(result.sections)
                    nextPage = result.nextPage
                    totalPages = result.totalPages
                    _uiState.value = if (allSections.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(allSections.toList())
                    }
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(error.toErrorStringRes())
                },
            )
        }
    }

    fun loadMore() {
        if (isLoadingMore) return
        val total = totalPages
        if (total != null && currentPage >= total) return

        val nextPageNum = resolveNextPageNumber() ?: return
        if (nextPageNum <= currentPage) return
        if (total != null && nextPageNum > total) return

        isLoadingMore = true
        _uiState.value = HomeUiState.Success(
            sections = allSections.toList(),
            isLoadingMore = true,
        )

        viewModelScope.launch {
            getHomeSectionsUseCase(nextPageNum).fold(
                onSuccess = { result ->
                    currentPage = nextPageNum
                    allSections.addAll(result.sections)
                    nextPage = result.nextPage
                    totalPages = result.totalPages ?: totalPages
                    _uiState.value = HomeUiState.Success(allSections.toList())
                },
                onFailure = {
                    // Keep existing content visible, just stop loading indicator
                    _uiState.value = HomeUiState.Success(allSections.toList())
                },
            )
            isLoadingMore = false
        }
    }

    private fun resolveNextPageNumber(): Int? {
        val rawNextPage = nextPage
        val parsedPage = rawNextPage
            ?.takeIf { it.isNotBlank() }
            ?.let { PAGE_QUERY_REGEX.find(it)?.groupValues?.getOrNull(1)?.toIntOrNull() }

        if (parsedPage != null) return parsedPage
        if (rawNextPage.isNullOrBlank()) return null

        val total = totalPages ?: return null
        return if (currentPage < total) currentPage + 1 else null
    }

    fun retry() {
        loadHomeSections()
    }
}
