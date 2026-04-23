package com.imp.samachardaily.domain.model

data class User(
    val id: String,
    val email: String?,
    val phone: String?,
    val name: String,
    val avatarUrl: String?,
    val preferredLanguages: List<String>,
    val createdAt: Long
)

