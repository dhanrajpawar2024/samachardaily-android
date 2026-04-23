package com.imp.samachardaily.data.repository

import androidx.paging.*
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.data.local.mapper.toDomain
import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : SearchRepository {

    override fun search(
        query: String,
        categoryId: String?,
        language: String?
    ): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchPagingSource(apiService, query, categoryId, language)
        }
    ).flow

    inner class SearchPagingSource(
        private val apiService: NewsApiService,
        private val query: String,
        private val categoryId: String?,
        private val language: String?
    ) : PagingSource<Int, Article>() {

        override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
            state.anchorPosition?.let { anchor ->
                state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
            }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
            val page = params.key ?: 1
            return try {
                val response = apiService.search(
                    query    = query,
                    category = categoryId,
                    language = language,
                    page     = page,
                    limit    = params.loadSize
                )
                if (!response.success || response.data == null) {
                    return LoadResult.Error(Exception(response.error ?: "Search failed"))
                }
                val data = response.data
                LoadResult.Page(
                    data    = data.articles.map { it.toDomain() },
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page >= data.totalPages) null else page + 1
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }
}

