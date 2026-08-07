package sutanu.apps.zenith.domain.model

enum class AlertSeverity { EXCEEDED, APPROACHING }
data class HomeAlert(
    val packageName: String? = null,
    val appName: String? = null,
    val alertType: AlertSeverity? = null,
    val timeStamp: String? = null,
    val usage: String? = null,
    val limit: String? = null
)
