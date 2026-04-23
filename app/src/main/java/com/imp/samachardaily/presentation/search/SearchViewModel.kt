package com.imp.samachardaily.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedLanguage: String? = null,
    val selectedCategoryId: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<Article>> = _uiState
        .debounce(400)
        .filter { it.query.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { state ->
            searchNewsUseCase(state.query, state.selectedCategoryId, state.selectedLanguage)
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun setLanguageFilter(language: String?) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun setCategoryFilter(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun clearQuery() {
        _uiState.update { it.copy(query = "") }
    }
}

