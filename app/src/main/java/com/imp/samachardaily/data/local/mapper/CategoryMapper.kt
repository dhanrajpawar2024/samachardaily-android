package com.imp.samachardaily.data.local.mapper

import com.imp.samachardaily.core.common.parseIso8601
import com.imp.samachardaily.core.database.CategoryEntity
import com.imp.samachardaily.data.remote.dto.CategoryDto
import com.imp.samachardaily.data.remote.dto.UserDto
import com.imp.samachardaily.domain.model.Category
import com.imp.samachardaily.domain.model.User

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    slug = slug,
    language = language,
    iconUrl = iconUrl,
    isActive = isActive
)

fun CategoryDto.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    slug = slug,
    language = language,
    iconUrl = iconUrl,
    isActive = isActive
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    slug = slug,
    language = language,
    iconUrl = iconUrl,
    isActive = isActive
)

fun UserDto.toDomain(): User = User(
    id               = id,
    email            = email,
    phone            = phone,
    name             = name,
    avatarUrl        = avatarUrl,
    preferredLanguages = preferredLanguages,
    createdAt        = createdAt?.parseIso8601() ?: System.currentTimeMillis()
)

