package com.imp.samachardaily.domain.model

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val language: String,
    val iconUrl: String?,
    val isActive: Boolean
)

