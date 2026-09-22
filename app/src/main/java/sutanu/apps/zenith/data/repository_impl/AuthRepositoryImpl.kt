package sutanu.apps.zenith.data.repository_impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override val parentalPin: Flow<String> = authPreferences.parentalPin

    override val isUninstallProtectionEnabled: Flow<Boolean> = authPreferences.isUninstallProtectionEnabled

    override val isDeviceAdminActive: Flow<Boolean> = authPreferences.isUninstallProtectionEnabled

    override suspend fun savePin(pin: String) {
        authPreferences.savePin(pin)
    }

    override suspend fun setUninstallProtection(enabled: Boolean) {
        authPreferences.setUninstallProtectionEnabled(enabled)
    }

    override val themeMode: Flow<String> = authPreferences.themeMode

    override suspend fun setThemeMode(mode: String) {
        authPreferences.setThemeMode(mode)
    }
}
