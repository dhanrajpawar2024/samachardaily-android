package com.imp.samachardaily.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_ID = stringPreferencesKey("user_id")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val SELECTED_CATEGORIES = stringPreferencesKey("selected_categories")
        val SELECTED_LANGUAGES = stringPreferencesKey("selected_languages")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val FOLLOWED_SOURCES = stringPreferencesKey("followed_sources")
        val TEXT_SIZE = floatPreferencesKey("text_size")
    }

    val preferredLanguage: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.PREFERRED_LANGUAGE] ?: "en" }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.ONBOARDING_COMPLETED] ?: false }

    val userId: Flow<String?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.USER_ID] }

    val authToken: Flow<String?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.AUTH_TOKEN] }

    val refreshToken: Flow<String?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.REFRESH_TOKEN] }

    val selectedCategories: Flow<List<String>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            prefs[Keys.SELECTED_CATEGORIES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }

    val selectedLanguages: Flow<Set<String>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            prefs[Keys.SELECTED_LANGUAGES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toSet()
                ?: setOf("en")
        }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.DARK_MODE] ?: false }

    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.NOTIFICATIONS_ENABLED] ?: true }

    val followedSources: Flow<Set<String>> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            prefs[Keys.FOLLOWED_SOURCES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toSet()
                ?: emptySet()
        }

    val textSize: Flow<Float> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.TEXT_SIZE] ?: 1.0f }

    suspend fun setPreferredLanguage(lang: String) =
        dataStore.edit { it[Keys.PREFERRED_LANGUAGE] = lang }

    suspend fun setOnboardingCompleted(completed: Boolean) =
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }

    suspend fun setUserId(id: String?) = dataStore.edit {
        if (id != null) it[Keys.USER_ID] = id else it.remove(Keys.USER_ID)
    }

    suspend fun setAuthToken(token: String?) = dataStore.edit {
        if (token != null) it[Keys.AUTH_TOKEN] = token else it.remove(Keys.AUTH_TOKEN)
    }

    suspend fun setRefreshToken(token: String?) = dataStore.edit {
        if (token != null) it[Keys.REFRESH_TOKEN] = token else it.remove(Keys.REFRESH_TOKEN)
    }

    suspend fun setSelectedCategories(categoryIds: List<String>) =
        dataStore.edit { it[Keys.SELECTED_CATEGORIES] = categoryIds.joinToString(",") }

    suspend fun setSelectedLanguages(languages: Set<String>) =
        dataStore.edit { it[Keys.SELECTED_LANGUAGES] = languages.joinToString(",") }

    suspend fun setDarkMode(enabled: Boolean) =
        dataStore.edit { it[Keys.DARK_MODE] = enabled }

    suspend fun setNotificationsEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }

    suspend fun setFcmToken(token: String) =
        dataStore.edit { it[Keys.FCM_TOKEN] = token }

    suspend fun setFollowedSources(sources: Set<String>) =
        dataStore.edit { it[Keys.FOLLOWED_SOURCES] = sources.joinToString(",") }

    suspend fun setTextSize(size: Float) =
        dataStore.edit { it[Keys.TEXT_SIZE] = size }

    suspend fun clearUserData() = dataStore.edit {
        it.remove(Keys.USER_ID)
        it.remove(Keys.AUTH_TOKEN)
        it.remove(Keys.REFRESH_TOKEN)
    }
}

