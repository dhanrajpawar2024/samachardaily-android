package com.imp.samachardaily.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoDto(
    @Json(name = "id")            val id: String,
    @Json(name = "title")         val title: String,
    @Json(name = "description")   val description: String?,
    @Json(name = "video_url")     val videoUrl: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "author_id")     val authorId: String?,
    @Json(name = "author_name")   val authorName: String?,
    @Json(name = "duration_ms")   val durationMs: Long,
    @Json(name = "language")      val language: String,
    @Json(name = "category_id")   val categoryId: String?,
    @Json(name = "category_name") val categoryName: String?,
    @Json(name = "view_count")    val viewCount: Int,
    @Json(name = "like_count")    val likeCount: Int,
    @Json(name = "share_count")   val shareCount: Int,
    @Json(name = "published_at")  val publishedAt: String?,
    @Json(name = "created_at")    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class VideoListData(
    @Json(name = "videos")     val videos: List<VideoDto>,
    @Json(name = "pagination") val pagination: VideoPaginationDto?
)

@JsonClass(generateAdapter = true)
data class VideoPaginationDto(
    @Json(name = "page")        val page: Int,
    @Json(name = "limit")       val limit: Int,
    @Json(name = "total")       val total: Int,
    @Json(name = "total_pages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class VideoListResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data")    val data: VideoListData?
)

@JsonClass(generateAdapter = true)
data class VideoDetailData(
    @Json(name = "video") val video: VideoDto
)

@JsonClass(generateAdapter = true)
data class VideoDetailResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data")    val data: VideoDetailData?
)

@JsonClass(generateAdapter = true)
data class VideoActionResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data")    val data: VideoActionData?
)

@JsonClass(generateAdapter = true)
data class VideoActionData(
    @Json(name = "video_id")   val videoId: String,
    @Json(name = "view_count") val viewCount: Int?,
    @Json(name = "like_count") val likeCount: Int?
)
