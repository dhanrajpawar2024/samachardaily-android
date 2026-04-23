package com.imp.samachardaily.core.database

import androidx.room.Entity

@Entity(tableName = "bookmarks", primaryKeys = ["userId", "articleId"])
data class BookmarkEntity(
    val userId: String,
    val articleId: String,
    val createdAt: Long = System.currentTimeMillis()
)

