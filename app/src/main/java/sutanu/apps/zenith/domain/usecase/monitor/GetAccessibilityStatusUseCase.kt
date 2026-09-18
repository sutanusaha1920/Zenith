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
        val expectedComponentName = ComponentName(context, AccessibilityMonitor::class.java)

        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val spitter = TextUtils.SimpleStringSplitter(':')
        spitter.setString(enabledServices)

        while (spitter.hasNext()) {
            val componentString = spitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        return false
    }
}