package com.imp.samachardaily.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val language: String,
    val iconUrl: String?,
    val isActive: Boolean
)

