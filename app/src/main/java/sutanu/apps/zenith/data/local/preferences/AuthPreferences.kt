package sutanu.apps.zenith.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Note: Ensure androidx.datastore:datastore-preferences is in your dependencies.
// The Preferences type must be from androidx.datastore.preferences.core, not java.util.prefs.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zenith_auth_prefs")

/**
 * Manages authentication-related preferences using Jetpack DataStore.
 *
 * Improvement: Injecting [DataStore] directly instead of [Context] for better testability.
 */
class AuthPreferences(private val context: Context) {

    companion object {
        private val KEY_PIN_HASH = stringPreferencesKey("parental_pin_hash")
        private val KEY_IS_DEVICE_ADMIN_ENABLED = booleanPreferencesKey("is_device_admin_enabled")
        private val KEY_TOTAL_SCREEN_TIME_LIMIT = stringPreferencesKey("total_screen_time_limit")
    }

    // Stream to observe PIN updates reactively
    val parentalPin: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PIN_HASH] ?: ""
    }

    // Save or update the authorization code
    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PIN_HASH] = pin
        }
    }

    // Check if device admin settings are marked active locally
    val isDeviceAdminEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_DEVICE_ADMIN_ENABLED] ?: false
    }

    suspend fun setDeviceAdminEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_DEVICE_ADMIN_ENABLED] = enabled
        }
    }

//    val totalScreenTimeLimit: Flow<String> = context.dataStore.data.map { preferences ->
//        preferences[KEY_TOTAL_SCREEN_TIME_LIMIT] ?: ""
//    }
//
//    suspend fun setTotalScreenTimeLimit(limit: String) {
//        context.dataStore.edit { preferences ->
//            preferences[KEY_TOTAL_SCREEN_TIME_LIMIT] = limit
//        }
//    }
}
