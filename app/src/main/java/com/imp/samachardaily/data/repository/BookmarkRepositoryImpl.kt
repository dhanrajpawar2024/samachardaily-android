package com.imp.samachardaily.data.repository

import androidx.paging.*
import androidx.room.withTransaction
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.database.AppDatabase
import com.imp.samachardaily.core.database.BookmarkEntity
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.core.network.safeApiCall
import com.imp.samachardaily.data.local.mapper.toEntity
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.data.remote.dto.BookmarkRequestDto
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.BookmarkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val database: AppDatabase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : BookmarkRepository {

    override fun getBookmarks(): Flow<PagingData<Article>> {
        return userPreferencesDataStore.userId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(PagingData.empty())
                } else {
                    flow {
                        syncRemoteBookmarks(userId)
                        emitAll(
                            Pager(
                                config = PagingConfig(
                                    pageSize = Constants.DEFAULT_PAGE_SIZE,
                                    enablePlaceholders = false
                                ),
                                pagingSourceFactory = {
                                    database.bookmarkDao().getBookmarkedArticles(userId)
                                }
                            ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
                        )
                    }
                }
            }
    }

    override fun isBookmarked(articleId: String): Flow<Boolean> {
        return userPreferencesDataStore.userId
            .flatMapLatest { userId ->
                if (userId == null) flowOf(false)
                else database.bookmarkDao().isBookmarked(userId, articleId)
            }
    }

    override suspend fun addBookmark(articleId: String): NetworkResult<Unit> {
        val userId = userPreferencesDataStore.userId.firstOrNull()
            ?: return NetworkResult.Error(message = "User not logged in")

        // Optimistic local insert
        database.bookmarkDao().addBookmark(BookmarkEntity(userId = userId, articleId = articleId))

        return safeApiCall { apiService.addBookmark(BookmarkRequestDto(articleId)) }.let { result ->
            when (result) {
                is NetworkResult.Success -> if (result.data.success) NetworkResult.Success(Unit)
                                            else NetworkResult.Error(message = result.data.error ?: "Failed")
                is NetworkResult.Error -> result
                NetworkResult.Loading  -> NetworkResult.Loading
            }
        }
    }

    override suspend fun removeBookmark(articleId: String): NetworkResult<Unit> {
        val userId = userPreferencesDataStore.userId.firstOrNull()
            ?: return NetworkResult.Error(message = "User not logged in")

        // Optimistic local delete
        database.bookmarkDao().removeBookmark(userId, articleId)

        return safeApiCall { apiService.removeBookmark(articleId) }.let { result ->
            when (result) {
                is NetworkResult.Success -> if (result.data.success) NetworkResult.Success(Unit)
                                            else NetworkResult.Error(message = result.data.error ?: "Failed")
                is NetworkResult.Error -> result
                NetworkResult.Loading  -> NetworkResult.Loading
            }
        }
    }

    private suspend fun syncRemoteBookmarks(userId: String) {
        val result = safeApiCall { apiService.getBookmarks(limit = 100) }
        if (result is NetworkResult.Success && result.data.success && result.data.data != null) {
            val bookmarks = result.data.data.bookmarks
            database.withTransaction {
                database.articleDao().insertArticles(bookmarks.map { it.toEntity() })
                database.bookmarkDao().clearBookmarksForUser(userId)
                database.bookmarkDao().addBookmarks(
                    bookmarks.map { article ->
                        BookmarkEntity(userId = userId, articleId = article.id)
                    }
                )
            }
        }
    }
}

