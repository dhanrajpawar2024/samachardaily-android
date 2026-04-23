package com.imp.samachardaily.domain.model

data class VideoItem(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val authorName: String?,
    val duration: Long,   // millis
    val viewCount: Int,
    val likeCount: Int,
    val language: String,
    val publishedAt: Long
)

