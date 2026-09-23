package sutanu.apps.zenith.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sutanu.apps.zenith.core.util.SecurityUtils
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zenith_auth_prefs")

/**
 * Manages authentication and bedtime preferences using Jetpack DataStore.
 */
@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val KEY_PIN_HASH = stringPreferencesKey("parental_pin_hash")
        private val KEY_IS_DEVICE_ADMIN_ENABLED = booleanPreferencesKey("is_device_admin_enabled")
        private val KEY_DEVICE_SCREEN_TIME_LIMIT = floatPreferencesKey("total_screen_time_limit")
        private val KEY_IS_BEDTIME_ENABLED = booleanPreferencesKey("is_bedtime_enabled")
        private val KEY_BEDTIME_START = stringPreferencesKey("bedtime_start_time")
        private val KEY_BEDTIME_END = stringPreferencesKey("bedtime_end_time")

        private val KEY_BEDTIME_ALLOW_CALLS = booleanPreferencesKey("bedtime_allow_calls")
        private val KEY_BEDTIME_ALLOW_ALARMS = booleanPreferencesKey("bedtime_allow_alarms")
        private val KEY_BEDTIME_ALLOW_WIFI = booleanPreferencesKey("bedtime_allow_wifi")

        private val KEY_UNINSTALL_PROTECTION_ENABLED = booleanPreferencesKey("is_uninstall_protection_enabled")
        private val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")

        private val KEY_DEVICE_OVERRIDE_EXTENSION_MINUTES = intPreferencesKey("device_override_extension_mins")
        private val KEY_APP_OVERRIDE_EXTENSION_MINUTES = intPreferencesKey("app_override_extension_mins")
        private val KEY_DEVICE_OVERRIDE_EXPIRATION_TIMESTAMP = longPreferencesKey("device_override_exp_timestamp")
    }

    // Stream to observe PIN updates reactively
    val parentalPin: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PIN_HASH] ?: ""
    }

    // Save or update the authorization code
    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PIN_HASH] = SecurityUtils.hashPin(pin)
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

    val isBedtimeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_BEDTIME_ENABLED] ?: false
    }

    suspend fun setBedtimeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_BEDTIME_ENABLED] = enabled
        }
    }

    val bedtimeStartTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BEDTIME_START] ?: "22:00"
    }

    val bedtimeEndTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_BEDTIME_END] ?: "07:00"
    }

    suspend fun saveBedTimeWindow(start: String, end: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BEDTIME_START] = start
            preferences[KEY_BEDTIME_END] = end
        }
    }

    val bedtimeAllowCalls: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_BEDTIME_ALLOW_CALLS] ?: true
    }

    suspend fun setBedtimeAllowCalls(allow: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BEDTIME_ALLOW_CALLS] = allow
        }
    }

    val bedtimeAllowAlarms: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_BEDTIME_ALLOW_ALARMS] ?: true
    }

    suspend fun setBedtimeAllowAlarms(allow: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BEDTIME_ALLOW_ALARMS] = allow
        }
    }

    val bedtimeAllowWifi: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_BEDTIME_ALLOW_WIFI] ?: true
    }

    suspend fun setBedtimeAllowWifi(allow: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BEDTIME_ALLOW_WIFI] = allow
        }
    }

    val deviceScreenTimeLimit: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEVICE_SCREEN_TIME_LIMIT] ?: 5.0f
    }

    suspend fun setDeviceScreenTimeLimit(limit: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEVICE_SCREEN_TIME_LIMIT] = limit
        }
    }

    val isUninstallProtectionEnabled = context.dataStore.data.map { preferences ->
        preferences[KEY_UNINSTALL_PROTECTION_ENABLED] ?: true
    }

    suspend fun setUninstallProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_UNINSTALL_PROTECTION_ENABLED] = enabled
        }
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "dark"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    // Device Override Extension
    val deviceOverrideExtensionMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEVICE_OVERRIDE_EXTENSION_MINUTES] ?: 30
    }

    suspend fun saveDeviceOverrideExtensionMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEVICE_OVERRIDE_EXTENSION_MINUTES] = minutes
        }
    }

    val deviceOverrideExpirationTimestamp: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_DEVICE_OVERRIDE_EXPIRATION_TIMESTAMP] ?: 0L
    }

    suspend fun grantDeviceOverrideExtension(extensionMinutes: Int) {
        val expiration = System.currentTimeMillis() + (extensionMinutes * 60 * 1000L)
        context.dataStore.edit { preferences ->
            preferences[KEY_DEVICE_OVERRIDE_EXPIRATION_TIMESTAMP] = expiration
        }
    }

    // App Override Extension
    val appOverrideExtensionMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_APP_OVERRIDE_EXTENSION_MINUTES] ?: 15
    }

    suspend fun saveAppOverrideExtensionMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_OVERRIDE_EXTENSION_MINUTES] = minutes
        }
    }
}
