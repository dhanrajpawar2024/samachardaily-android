package com.imp.samachardaily.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.model.Category
import com.imp.samachardaily.domain.usecase.GetCategoriesUseCase
import com.imp.samachardaily.domain.usecase.GetFeedUseCase
import com.imp.samachardaily.domain.usecase.GetTrendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedLanguages: Set<String> = setOf(Constants.LANG_ENGLISH),
    // Primary language drives categories UI
    val primaryLanguage: String = Constants.LANG_ENGLISH,
    val isLoadingCategories: Boolean = false,
    val categoriesError: String? = null,
    val trendingArticles: List<Article> = emptyList(),
    val isTrendingLoading: Boolean = true,   // start true so section shows on launch
    val trendingError: String? = null,
    val followedSources: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTrendingUseCase: GetTrendingUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val feed: Flow<PagingData<Article>> = _uiState
        .map { Triple(it.primaryLanguage, it.selectedCategory?.id, it.selectedLanguages) }
        .distinctUntilChanged()
        .flatMapLatest { (primaryLang, catId, langs) ->
            getFeedUseCase(primaryLang, catId, langs)
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            // Load selected languages from datastore
            val selectedLangs = userPreferencesDataStore.selectedLanguages.first()
                .ifEmpty { setOf(Constants.LANG_ENGLISH) }
            val primary = userPreferencesDataStore.preferredLanguage.first()
                .ifBlank { selectedLangs.first() }

            _uiState.update {
                it.copy(selectedLanguages = selectedLangs, primaryLanguage = primary)
            }
            loadCategories(primary)
            loadTrending(primary)
        }

        viewModelScope.launch {
            userPreferencesDataStore.followedSources.collect { sources ->
                _uiState.update { it.copy(followedSources = sources) }
            }
        }
    }

    private fun loadCategories(language: String) {
        viewModelScope.launch {
            getCategoriesUseCase(language).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoadingCategories = true) }
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(isLoadingCategories = false, categories = result.data, categoriesError = null)
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoadingCategories = false, categoriesError = result.message)
                    }
                }
            }
        }
    }

    private fun loadTrending(language: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTrendingLoading = true, trendingError = null) }
            when (val result = getTrendingUseCase(language, limit = 10)) {
                is NetworkResult.Success -> _uiState.update {
                    it.copy(isTrendingLoading = false, trendingArticles = result.data, trendingError = null)
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(isTrendingLoading = false, trendingError = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun retryTrending() = loadTrending(_uiState.value.primaryLanguage)

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
