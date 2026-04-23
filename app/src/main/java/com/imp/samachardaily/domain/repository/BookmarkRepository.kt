package com.imp.samachardaily.domain.repository

import androidx.paging.PagingData
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<PagingData<Article>>
    fun isBookmarked(articleId: String): Flow<Boolean>
    suspend fun addBookmark(articleId: String): NetworkResult<Unit>
    suspend fun removeBookmark(articleId: String): NetworkResult<Unit>
}

