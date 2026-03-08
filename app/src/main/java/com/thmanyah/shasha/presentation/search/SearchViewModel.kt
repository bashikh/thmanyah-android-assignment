package com.thmanyah.shasha.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thmanyah.shasha.core.util.toErrorStringRes
import com.thmanyah.shasha.domain.usecase.SearchSectionsUseCase
import com.thmanyah.shasha.presentation.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSectionsUseCase: SearchSectionsUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = query
        .debounce(200)
        .distinctUntilChanged()
        .flatMapLatest { searchQuery ->
            if (searchQuery.isBlank()) {
                flow { emit(SearchUiState.Idle) }
            } else {
                flow {
                    emit(SearchUiState.Loading)
                    searchSectionsUseCase(searchQuery).fold(
                        onSuccess = { sections ->
                            emit(
                                if (sections.isEmpty()) SearchUiState.Empty
                                else SearchUiState.Success(sections)
                            )
                        },
                        onFailure = { error ->
                            emit(SearchUiState.Error(error.toErrorStringRes()))
                        },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState.Idle)

    fun onQueryChanged(newQuery: String) {
        query.value = newQuery
    }

    fun retry() {
        val currentQuery = query.value
        if (currentQuery.isNotBlank()) {
            // Force re-emission by resetting and re-setting the query
            query.value = ""
            query.value = currentQuery
        }
    }
}
