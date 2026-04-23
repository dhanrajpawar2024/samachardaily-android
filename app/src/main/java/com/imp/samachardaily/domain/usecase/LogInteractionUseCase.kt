package com.imp.samachardaily.domain.usecase

import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.model.Interaction
import com.imp.samachardaily.domain.repository.ArticleRepository
import javax.inject.Inject

class LogInteractionUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(interaction: Interaction): NetworkResult<Unit> =
        articleRepository.logInteraction(interaction)
}

