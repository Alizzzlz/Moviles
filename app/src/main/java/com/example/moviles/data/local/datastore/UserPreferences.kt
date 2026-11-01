package com.example.moviles.data.local.datastore

import android.R.attr.name
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "obra_check_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")

        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_RUT = stringPreferencesKey("user_rut")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")

        private val KEY_USER_IMAGE_URI = stringPreferencesKey("user_image_uri")
    }

    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: ""
    }

    val userRut: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_RUT] ?: ""
    }
    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: ""
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: false
    }

    val userPhone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_PHONE] ?: ""
    }

    val userImageUri: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_IMAGE_URI]
    }

    suspend fun saveAuthToken(token: String, rut: String,name: String = "",
                              email: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_RUT] = rut
            if (name.isNotBlank()) {prefs[KEY_USER_NAME] = name}
            if (email.isNotBlank()) {
                prefs[KEY_USER_EMAIL] = email
            }
        }
    }

    suspend fun saveContactInfo(email: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_PHONE] = phone
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER_RUT)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_EMAIL)
        }
    }

    suspend fun saveProfileImageUri(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_IMAGE_URI] = uri
        }
    }
}