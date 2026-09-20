package sutanu.apps.zenith.data.services

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.dao.SosContactsDao
import javax.inject.Inject

@AndroidEntryPoint
class EmergencySosService : LifecycleService() {

    @Inject
    lateinit var sosContactsDao: SosContactsDao

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("ZenithSOS", "Executing background emergency location fetch...")
        fetchCoordinatesAndSendSms()
        return START_NOT_STICKY
    }

    private fun fetchCoordinatesAndSendSms() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            Log.e("ZenithSOS", "Location permissions missing. Dispatching fallback SMS.")
            dispatchSmsToTrustedContacts("HELP! Emergency triggered from Zenith App. Location permissions missing.")
            return
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    val mapLink = if (location != null) {
                        "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    } else {
                        "Coordinates Unavailable (GPS weak)"
                    }
                    val smsMessage =
                        "HELP! I need assistance. My location : $mapLink - Sent via Zenith App"
                    dispatchSmsToTrustedContacts(smsMessage)
                }
                .addOnFailureListener { e ->
                    Log.e("ZenithSOS", "Failed fetching GPS coordinates", e)
                    dispatchSmsToTrustedContacts("HELP! Emergency triggered from Zenith App. Unable to fetch real-time GPS link.")
                }
        } catch (e: SecurityException) {
            Log.e("ZenithSOS", "SecurityException fetching location data", e)
            dispatchSmsToTrustedContacts("HELP! Emergency triggered from Zenith App.")
        }
    }

    private fun dispatchSmsToTrustedContacts(message: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val phoneNumbers = sosContactsDao.getAllContactNumbers()
                if (phoneNumbers.isEmpty()) {
                    Log.e("ZenithSOS", "No trusted contacts found in database.")
                    return@launch
                }

                if (ContextCompat.checkSelfPermission(
                        this@EmergencySosService,
                        Manifest.permission.SEND_SMS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("ZenithSOS", "SEND_SMS permission not granted.")
                    return@launch
                }

                val smsManager: SmsManager =
                    this@EmergencySosService.getSystemService(SmsManager::class.java)
                for (number in phoneNumbers) {
                    if (number.isNotBlank()) {
                        smsManager.sendTextMessage(number, null, message, null, null)
                        Log.d("ZenithSOS", "Emergency message sent to $number")
                    }
                }
                Log.d("ZenithSOS", "Emergency messages broadcasted successfully.")
            } catch (e: Exception) {
                Log.e("ZenithSOS", "Failed SMS dispatch transmission", e)
            } finally {
                stopSelf()
            }
        }
    }
}
