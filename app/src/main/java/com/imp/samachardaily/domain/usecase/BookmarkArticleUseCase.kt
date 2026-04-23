package com.imp.samachardaily.domain.usecase

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkArticleUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    fun isBookmarked(articleId: String): Flow<Boolean> =
        bookmarkRepository.isBookmarked(articleId)

    suspend fun addBookmark(articleId: String): NetworkResult<Unit> =
        bookmarkRepository.addBookmark(articleId)

    suspend fun removeBookmark(articleId: String): NetworkResult<Unit> =
        bookmarkRepository.removeBookmark(articleId)
}

