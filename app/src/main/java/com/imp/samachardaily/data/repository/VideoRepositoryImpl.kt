package com.imp.samachardaily.data.repository

import com.imp.samachardaily.data.remote.NewsApiService
import com.imp.samachardaily.data.remote.dto.VideoDto
import com.imp.samachardaily.domain.model.VideoItem
import com.imp.samachardaily.domain.repository.VideoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : VideoRepository {

    override suspend fun getVideos(
        page: Int,
        limit: Int,
        language: String?,
        categoryId: String?
    ): Result<List<VideoItem>> = runCatching {
        val response = apiService.getVideos(
            page = page,
            limit = limit,
            language = language,
            categoryId = categoryId
        )
        response.data.videos.map { it.toDomain() }
    }

    override suspend fun incrementView(videoId: String): Result<Int> = runCatching {
        val response = apiService.incrementVideoView(videoId)
        response.data?.viewCount ?: 0
    }

    override suspend fun likeVideo(videoId: String): Result<Int> = runCatching {
        val response = apiService.likeVideo(videoId)
        response.data?.likeCount ?: 0
    }

    private fun VideoDto.toDomain() = VideoItem(
        id           = id,
        title        = title,
        videoUrl     = videoUrl,
        thumbnailUrl = thumbnailUrl,
        authorName   = authorName,
        duration     = durationMs,
        viewCount    = viewCount,
        likeCount    = likeCount,
        language     = language,
        publishedAt  = 0L  // parsed separately if needed
    )
}
