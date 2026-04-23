package com.imp.samachardaily.domain.usecase

import androidx.paging.PagingData
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<PagingData<Article>> = bookmarkRepository.getBookmarks()
}

