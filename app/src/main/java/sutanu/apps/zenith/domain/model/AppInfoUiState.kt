package sutanu.apps.zenith.domain.model

import android.graphics.drawable.Drawable

data class AppInfoUiState(
    val packageName: String,
    val appName: String,
    val icon: Drawable?
)
