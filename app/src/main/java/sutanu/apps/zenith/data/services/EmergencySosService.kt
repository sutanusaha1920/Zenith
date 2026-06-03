package sutanu.apps.zenith.data.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.ZenithDatabase

class EmergencySosService : Service() {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ZenithSOS", "Executing background emergency location fetch...")
        fetchCoordinatesAndSendSms()
        return START_NOT_STICKY
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchCoordinatesAndSendSms() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            // Fetch the user's current GPS data
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
                .addOnFailureListener {
                    // Send only SOS message if location hardware is not working
                    dispatchSmsToTrustedContacts("HELP! Emergency triggered from Zenith App. Unable to fetch real-time GPS link.")
                }
        } catch (e: SecurityException) {
            Log.e("ZenithSOS", "Cannot transmit SMS: Location Permissions missing.", e)
            stopSelf()
        }
    }

    private fun dispatchSmsToTrustedContacts(message: String) {

        val db = ZenithDatabase.getDatabase(this)

        kotlinx.coroutines.MainScope().launch(kotlinx.coroutines.Dispatchers.IO) {
            val phoneNumbers = db.sosContactsDao().getAllContactNumbers()

            if (phoneNumbers.isEmpty()) {
                Log.e("ZenithSOS", "No trusted contacts found in the database.")
                return@launch
            }

            try {
                val smsManager: SmsManager =
                    this@EmergencySosService.getSystemService(SmsManager::class.java)
                for (number in phoneNumbers) {
                    smsManager.sendTextMessage(number, null, message, null, null)
                    Log.d("ZenithSOS", "Emergency message sent to $number")
                }
                Log.d("ZenithSOS", "Emergency message broadcasted successfully.")
            } catch (e: Exception) {
                Log.e("ZenithSOS", "Failed offline cellular transport transmission", e)
            } finally {
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}