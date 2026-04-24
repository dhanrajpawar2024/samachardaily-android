package com.imp.samachardaily.domain.repository

import com.imp.samachardaily.domain.model.VideoItem

interface VideoRepository {
    suspend fun getVideos(
        page: Int = 1,
        limit: Int = 20,
        language: String? = null,
        categoryId: String? = null
    ): Result<List<VideoItem>>

    suspend fun incrementView(videoId: String): Result<Int>

    suspend fun likeVideo(videoId: String): Result<Int>
}
