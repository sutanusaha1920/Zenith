package sutanu.apps.zenith.domain.model

import android.graphics.drawable.Drawable

data class AppLaunchInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val lastLaunchedText: String,
    val usageText: String,
    val isLimitExceeded: Boolean
)
