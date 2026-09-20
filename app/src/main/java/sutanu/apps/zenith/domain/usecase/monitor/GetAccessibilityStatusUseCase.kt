package sutanu.apps.zenith.domain.usecase.monitor

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import sutanu.apps.zenith.data.services.AccessibilityMonitor
import javax.inject.Inject

class GetAccessibilityStatusUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1
        } catch (_: Exception) {
            false
        }
        if (!accessibilityEnabled) return false

        val expectedComponentName = ComponentName(context, AccessibilityMonitor::class.java)

        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)

        while (splitter.hasNext()) {
            val componentString = splitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentString)
            if (enabledComponent != null &&
                enabledComponent.packageName == context.packageName &&
                enabledComponent.className == expectedComponentName.className
            ) {
                return true
            }
        }
        return false
    }
}