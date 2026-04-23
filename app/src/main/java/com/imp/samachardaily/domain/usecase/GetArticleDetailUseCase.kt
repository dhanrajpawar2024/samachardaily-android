package com.imp.samachardaily.domain.usecase

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleDetailUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    operator fun invoke(articleId: String): Flow<NetworkResult<Article>> =
        articleRepository.getArticleDetail(articleId)
}

