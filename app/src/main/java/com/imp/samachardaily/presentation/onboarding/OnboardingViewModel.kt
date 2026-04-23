package com.imp.samachardaily.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.repository.UserRepository
import com.imp.samachardaily.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (userPreferencesDataStore.isOnboardingCompleted.first()) {
                _uiState.update {
                    it.copy(
                        step = OnboardingStep.LOGIN,
                        selectedLanguages = setOf(userPreferencesDataStore.preferredLanguage.first()),
                        selectedCategoryIds = userPreferencesDataStore.selectedCategories.first().toSet()
                    )
                }
            }
        }
    }

    fun toggleLanguage(lang: String) {
        _uiState.update { state ->
            val updated = if (lang in state.selectedLanguages)
                state.selectedLanguages - lang
            else
                state.selectedLanguages + lang
            state.copy(selectedLanguages = updated)
        }
    }

    fun toggleCategory(categoryId: String) {
        _uiState.update { state ->
            val updated = if (categoryId in state.selectedCategoryIds)
                state.selectedCategoryIds - categoryId
            else
                state.selectedCategoryIds + categoryId
            state.copy(selectedCategoryIds = updated)
        }
    }

    fun proceedFromLanguage() {
        val langs = _uiState.value.selectedLanguages
        if (langs.isEmpty()) return
        loadCategories(langs.first())
        _uiState.update { it.copy(step = OnboardingStep.CATEGORIES) }
    }

    fun proceedFromCategories() {
        _uiState.update { it.copy(step = OnboardingStep.LOGIN, isLoading = false) }
    }

    /**
     * Called when the user taps "Sign in with Google" and we receive the idToken
     * from the ActivityResultLauncher in MainActivity.
     */
    fun loginWithGoogle(idToken: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.loginWithGoogle(idToken)
            when (result) {
                is NetworkResult.Success -> {
                    savePreferences()
                    _uiState.update { it.copy(isLoading = false) }
                    onComplete()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun skipLogin(onComplete: () -> Unit) {
        viewModelScope.launch {
            savePreferences()
            onComplete()
        }
    }

    private fun loadCategories(language: String) {
        viewModelScope.launch {
            getCategoriesUseCase(language).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(isLoading = false, availableCategories = result.data)
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    private suspend fun savePreferences() {
        val state = _uiState.value
        val primary = state.selectedLanguages.firstOrNull() ?: Constants.LANG_ENGLISH
        userPreferencesDataStore.setPreferredLanguage(primary)
        userPreferencesDataStore.setSelectedLanguages(state.selectedLanguages)
        userPreferencesDataStore.setSelectedCategories(state.selectedCategoryIds.toList())
        userPreferencesDataStore.setOnboardingCompleted(true)
    }
}
