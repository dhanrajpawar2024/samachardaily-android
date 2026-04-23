package com.imp.samachardaily.domain.repository

import androidx.paging.PagingData
import com.imp.samachardaily.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(
        query: String,
        categoryId: String? = null,
        language: String? = null
    ): Flow<PagingData<Article>>
}

