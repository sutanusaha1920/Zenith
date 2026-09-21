package sutanu.apps.zenith.data.repository_impl

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.data.services.ZenithAdminReceiver
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override val parentalPin: Flow<String> = authPreferences.parentalPin

    override val isUninstallProtectionEnabled: Flow<Boolean> = authPreferences.isUninstallProtectionEnabled

    override val isDeviceAdminActive: Flow<Boolean> = authPreferences.isDeviceAdminEnabled.map {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val adminComponent = ComponentName(context, ZenithAdminReceiver::class.java)

        dpm.isAdminActive(adminComponent)
    }

    override suspend fun savePin(pin: String) {
        authPreferences.savePin(pin)
    }

    override suspend fun setUninstallProtection(enabled: Boolean) {
        authPreferences.setUninstallProtectionEnabled(enabled)

        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

            val adminComponent = ComponentName(context, ZenithAdminReceiver::class.java)

            dpm.setUninstallBlocked(adminComponent, context.packageName, enabled)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override val themeMode: Flow<String> = authPreferences.themeMode

    override suspend fun setThemeMode(mode: String) {
        authPreferences.setThemeMode(mode)
    }

}