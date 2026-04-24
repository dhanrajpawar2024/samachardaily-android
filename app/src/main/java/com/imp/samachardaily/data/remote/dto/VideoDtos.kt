package com.imp.samachardaily.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id")            val id: String,
    @SerializedName("title")         val title: String,
    @SerializedName("description")   val description: String?,
    @SerializedName("video_url")     val videoUrl: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    @SerializedName("author_id")     val authorId: String?,
    @SerializedName("author_name")   val authorName: String?,
    @SerializedName("duration_ms")   val durationMs: Long,
    @SerializedName("language")      val language: String,
    @SerializedName("category_id")   val categoryId: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("view_count")    val viewCount: Int,
    @SerializedName("like_count")    val likeCount: Int,
    @SerializedName("share_count")   val shareCount: Int,
    @SerializedName("published_at")  val publishedAt: String?,
    @SerializedName("created_at")    val createdAt: String?
)

data class VideoListData(
    @SerializedName("videos")     val videos: List<VideoDto>,
    @SerializedName("pagination") val pagination: VideoPaginationDto
)

data class VideoPaginationDto(
    @SerializedName("page")        val page: Int,
    @SerializedName("limit")       val limit: Int,
    @SerializedName("total")       val total: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class VideoListResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: VideoListData
)

data class VideoDetailData(
    @SerializedName("video") val video: VideoDto
)

data class VideoDetailResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: VideoDetailData
)

data class VideoActionResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: VideoActionData?
)

data class VideoActionData(
    @SerializedName("video_id")   val videoId: String,
    @SerializedName("view_count") val viewCount: Int?,
    @SerializedName("like_count") val likeCount: Int?
)
