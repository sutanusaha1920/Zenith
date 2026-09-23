package sutanu.apps.zenith.core.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object BedtimeUtils {

    /**
     * Checks if the current local time falls within the bedtime schedule window.
     * Supports overnight windows crossing midnight (e.g., 22:00 to 07:00 or 10:00 PM to 07:00 AM).
     */
    fun isCurrentTimeInBedtimeWindow(startTimeStr: String, endTimeStr: String): Boolean {
        val start = parseTime(startTimeStr) ?: return false
        val end = parseTime(endTimeStr) ?: return false
        val now = LocalTime.now()

        return if (start.isBefore(end)) {
            // Same day window (e.g., 13:00 to 15:00)
            !now.isBefore(start) && now.isBefore(end)
        } else if (start.isAfter(end)) {
            // Overnight window crossing midnight (e.g., 22:00 to 07:00)
            !now.isBefore(start) || now.isBefore(end)
        } else {
            false
        }
    }

    private fun parseTime(timeStr: String): LocalTime? {
        val trimmed = timeStr.trim().uppercase(Locale.US)
        return try {
            LocalTime.parse(trimmed) // e.g. "22:00"
        } catch (e: Exception) {
            try {
                LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)) // e.g. "10:00 PM"
            } catch (e2: Exception) {
                try {
                    LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("h:mm a", Locale.US)) // e.g. "7:00 AM"
                } catch (e3: Exception) {
                    try {
                        LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("HH:mm", Locale.US))
                    } catch (e4: Exception) {
                        try {
                            LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("H:mm", Locale.US))
                        } catch (e5: Exception) {
                            null
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines whether an app package is allowed during Bedtime Mode according to the user's exception toggles.
     */
    fun isAppAllowedDuringBedtime(
        openedPackageName: String,
        zenithPackageName: String,
        allowCalls: Boolean,
        allowAlarms: Boolean,
        allowWifi: Boolean
    ): Boolean {
        val pkg = openedPackageName.lowercase(Locale.getDefault())

        // Zenith itself, LockScreenOverlay, and Home Launchers are always allowed
        if (pkg == zenithPackageName.lowercase(Locale.getDefault()) || pkg.contains("launcher") || pkg.contains("lockscreenoverlay")) {
            return true
        }

        // System Settings app
        if (pkg == "com.android.settings") return true

        // Phone calls / Dialer apps
        if (allowCalls) {
            if (pkg.contains("dialer") || pkg.contains("incallui") || pkg.contains("telecom") ||
                pkg.contains("contacts") || pkg.contains("phone") || pkg == "com.google.android.dialer" ||
                pkg == "com.android.dialer" || pkg == "com.samsung.android.dialer") {
                return true
            }
        }

        // Alarm clock apps
        if (allowAlarms) {
            if (pkg.contains("deskclock") || pkg.contains("clock") || pkg.contains("alarm")) {
                return true
            }
        }

        // Wi-Fi / Connectivity / Network settings apps
        if (allowWifi) {
            if (pkg.contains("wifi") || pkg.contains("network") || pkg.contains("connectivity")) {
                return true
            }
        }

        // All other apps (social media, games, browsers, YouTube, etc.) are BLOCKED during bedtime
        return false
    }
}
