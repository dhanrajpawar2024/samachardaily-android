package com.imp.samachardaily.presentation.article

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.model.Interaction
import com.imp.samachardaily.domain.model.InteractionAction
import com.imp.samachardaily.domain.usecase.BookmarkArticleUseCase
import com.imp.samachardaily.domain.usecase.GetArticleDetailUseCase
import com.imp.samachardaily.domain.usecase.LogInteractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ArticleUiState {
    object Loading : ArticleUiState()
    data class Success(
        val article: Article,
        val isBookmarked: Boolean = false,
        val isSourceFollowed: Boolean = false,
        val textSize: Float = 1.0f
    ) : ArticleUiState()
    data class Error(val message: String) : ArticleUiState()
}

@HiltViewModel
class ArticleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticleDetailUseCase: GetArticleDetailUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase,
    private val logInteractionUseCase: LogInteractionUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val articleId: String = checkNotNull(savedStateHandle["articleId"])

    private val _uiState = MutableStateFlow<ArticleUiState>(ArticleUiState.Loading)
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    init {
        loadArticle()
        logView()
        observePreferences()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            getArticleDetailUseCase(articleId).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> ArticleUiState.Loading
                    is NetworkResult.Success -> {
                        val isBookmarked = bookmarkArticleUseCase.isBookmarked(articleId).first()
                        val followedSources = userPreferencesDataStore.followedSources.first()
                        val textSize = userPreferencesDataStore.textSize.first()
                        ArticleUiState.Success(
                            article = result.data,
                            isBookmarked = isBookmarked,
                            isSourceFollowed = result.data.sourceName in followedSources,
                            textSize = textSize
                        )
                    }
                    is NetworkResult.Error -> ArticleUiState.Error(result.message)
                }
            }
        }

        viewModelScope.launch {
            bookmarkArticleUseCase.isBookmarked(articleId).collect { isBookmarked ->
                _uiState.update { state ->
                    if (state is ArticleUiState.Success) state.copy(isBookmarked = isBookmarked)
                    else state
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                userPreferencesDataStore.followedSources,
                userPreferencesDataStore.textSize
            ) { sources, size -> Pair(sources, size) }.collect { (sources, size) ->
                _uiState.update { state ->
                    if (state is ArticleUiState.Success)
                        state.copy(
                            isSourceFollowed = state.article.sourceName in sources,
                            textSize = size
                        )
                    else state
                }
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val currentState = _uiState.value as? ArticleUiState.Success ?: return@launch
            if (currentState.isBookmarked) bookmarkArticleUseCase.removeBookmark(articleId)
            else bookmarkArticleUseCase.addBookmark(articleId)
        }
    }

    fun toggleFollowSource() {
        viewModelScope.launch {
            val sourceName = (_uiState.value as? ArticleUiState.Success)?.article?.sourceName ?: return@launch
            val current = userPreferencesDataStore.followedSources.first()
            val updated = if (sourceName in current) current - sourceName else current + sourceName
            userPreferencesDataStore.setFollowedSources(updated)
        }
    }

    fun logShare() {
        viewModelScope.launch {
            logInteractionUseCase(Interaction(articleId, InteractionAction.SHARE))
        }
    }

    private fun logView() {
        viewModelScope.launch {
            logInteractionUseCase(Interaction(articleId, InteractionAction.VIEW))
        }
    }
}
