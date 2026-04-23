package com.imp.samachardaily.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.usecase.GetBookmarksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {
    val bookmarks: Flow<PagingData<Article>> = getBookmarksUseCase().cachedIn(viewModelScope)
}

