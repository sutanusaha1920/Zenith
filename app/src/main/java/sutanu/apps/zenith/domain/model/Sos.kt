package sutanu.apps.zenith.domain.model

import sutanu.apps.zenith.data.local.db.entity.SosContactEntity

data class Sos(
    val lastKnownLocation: String = "",
    val gpsAccuracy: String = "",
    val trustedContacts: List<SosContactEntity> = emptyList(),
    val isSendingAlert: Boolean = false,
    val showAddContactDialog: Boolean = false,
    val isLoadingContacts: Boolean = false,
)
