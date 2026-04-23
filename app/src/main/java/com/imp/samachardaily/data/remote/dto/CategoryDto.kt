package com.imp.samachardaily.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "language") val language: String,
    @Json(name = "icon_url") val iconUrl: String? = null,
    @Json(name = "is_active") val isActive: Boolean = true
)

