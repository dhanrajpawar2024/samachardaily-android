package com.imp.samachardaily.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.samachardaily.core.common.Constants
import com.imp.samachardaily.core.datastore.UserPreferencesDataStore
import com.imp.samachardaily.core.network.NetworkResult
import com.imp.samachardaily.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val preferredLanguage: String = "en",
    val selectedLanguages: Set<String> = setOf("en"),
    val selectedCategoryIds: List<String> = emptyList(),
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val followedSources: Set<String> = emptySet(),
    val textSize: Float = 1.0f,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val userAvatarUrl: String? = null,
    val isAccountLoading: Boolean = false,
    val authActionInProgress: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    private val accountState = MutableStateFlow(ProfileUiState())

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferencesDataStore.preferredLanguage,
        userPreferencesDataStore.selectedLanguages,
        userPreferencesDataStore.selectedCategories,
        userPreferencesDataStore.isDarkMode,
        userPreferencesDataStore.notificationsEnabled,
    ) { lang, langs, cats, dark, notifs ->
        accountState.value.copy(
            preferredLanguage = lang,
            selectedLanguages = langs,
            selectedCategoryIds = cats,
            isDarkMode = dark,
            notificationsEnabled = notifs,
        )
    }.combine(userPreferencesDataStore.userId) { state, userId ->
        state.copy(isLoggedIn = userId != null)
    }.combine(userPreferencesDataStore.followedSources) { state, sources ->
        state.copy(followedSources = sources)
    }.combine(userPreferencesDataStore.textSize) { state, size ->
        state.copy(textSize = size)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    init {
        viewModelScope.launch {
            userPreferencesDataStore.userId.collectLatest { userId ->
                if (userId != null) refreshProfile()
                else accountState.update {
                    it.copy(userName = null, userEmail = null, userAvatarUrl = null,
                        isAccountLoading = false, authActionInProgress = false, errorMessage = null)
                }
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { userPreferencesDataStore.setDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesDataStore.setNotificationsEnabled(enabled) }
    }

    fun toggleFollowSource(sourceName: String) {
        viewModelScope.launch {
            val current = userPreferencesDataStore.followedSources.first()
            val updated = if (sourceName in current) current - sourceName else current + sourceName
            userPreferencesDataStore.setFollowedSources(updated)
        }
    }

    fun setTextSize(size: Float) {
        viewModelScope.launch { userPreferencesDataStore.setTextSize(size) }
    }

    fun toggleLanguage(lang: String) {
        viewModelScope.launch {
            val current = userPreferencesDataStore.selectedLanguages.first()
            val updated = if (lang in current) {
                if (current.size > 1) current - lang else current // keep at least one
            } else {
                current + lang
            }
            userPreferencesDataStore.setSelectedLanguages(updated)
            // Update primary language to first selected
            userPreferencesDataStore.setPreferredLanguage(updated.first())
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            accountState.update { it.copy(authActionInProgress = true, errorMessage = null) }
            when (val result = userRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> accountState.update {
                    it.copy(authActionInProgress = false, userName = result.data.name,
                        userEmail = result.data.email, userAvatarUrl = result.data.avatarUrl, errorMessage = null)
                }
                is NetworkResult.Error -> accountState.update {
                    it.copy(authActionInProgress = false, errorMessage = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            accountState.update { it.copy(authActionInProgress = true, errorMessage = null) }
            when (val result = userRepository.logout()) {
                is NetworkResult.Success -> accountState.value = ProfileUiState(
                    preferredLanguage = accountState.value.preferredLanguage,
                    selectedLanguages = accountState.value.selectedLanguages,
                    selectedCategoryIds = accountState.value.selectedCategoryIds,
                    isDarkMode = accountState.value.isDarkMode,
                    notificationsEnabled = accountState.value.notificationsEnabled
                )
                is NetworkResult.Error -> accountState.update {
                    it.copy(authActionInProgress = false, errorMessage = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> accountState.update { it.copy(isAccountLoading = true, errorMessage = null) }
                    is NetworkResult.Success -> accountState.update {
                        it.copy(isAccountLoading = false, authActionInProgress = false,
                            userName = result.data.name, userEmail = result.data.email,
                            userAvatarUrl = result.data.avatarUrl, errorMessage = null)
                    }
                    is NetworkResult.Error -> accountState.update {
                        it.copy(isAccountLoading = false, authActionInProgress = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}
