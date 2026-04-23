package com.imp.samachardaily.presentation.onboarding

import com.imp.samachardaily.domain.model.Category

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.LANGUAGE,
    val selectedLanguages: Set<String> = emptySet(),
    val availableCategories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class OnboardingStep { LANGUAGE, CATEGORIES, LOGIN }

