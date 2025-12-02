package com.example.boxtrakr.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.boxtrakr.ui.theme.ThemeSetting

// Create the DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_setting")
    }

    // Get theme setting
    val themeSetting: Flow<ThemeSetting> = context.dataStore.data
        .map { preferences ->
            val setting = preferences[THEME_KEY] ?: ThemeSetting.SYSTEM.name
            ThemeSetting.valueOf(setting)
        }

    // Save theme setting
    suspend fun saveThemeSetting(themeSetting: ThemeSetting) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeSetting.name
        }
    }
}