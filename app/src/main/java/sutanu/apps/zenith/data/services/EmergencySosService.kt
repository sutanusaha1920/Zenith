package sutanu.apps.zenith.data.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import sutanu.apps.zenith.R
import sutanu.apps.zenith.data.local.db.dao.SosContactsDao
import javax.inject.Inject
import kotlin.coroutines.resume

@AndroidEntryPoint
class EmergencySosService : LifecycleService() {

    @Inject
    lateinit var sosContactsDao: SosContactsDao

    companion object {
        private const val CHANNEL_ID = "sos_emergency_channel"
        private const val NOTIFICATION_ID = 9991
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        try {
            startForeground(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            Log.e("ZenithSOS", "Failed to start foreground notification", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("ZenithSOS", "Executing background emergency location fetch...")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                fetchCoordinatesAndSendSms()
            } catch (e: Exception) {
                Log.e("ZenithSOS", "Unhandled error in EmergencySosService", e)
            } finally {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun fetchCoordinatesAndSendSms() {
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

        var locationMessage = "HELP! I need assistance. Sent via Zenith App."

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            val cts = CancellationTokenSource()

            // Fetch current location with an 8-second strict timeout
            val location: Location? = withTimeoutOrNull(8000) {
                try {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cts.token
                    ).awaitTask()
                } catch (e: Exception) {
                    Log.e("ZenithSOS", "Error awaiting location task", e)
                    null
                }
            } ?: run {
                Log.w("ZenithSOS", "getCurrentLocation timed out or returned null. Trying lastKnownLocation fallback.")
                try {
                    fusedLocationClient.lastLocation.awaitTask()
                } catch (e: Exception) {
                    null
                }
            }

            locationMessage = if (location != null) {
                "HELP! I need assistance. My location : https://maps.google.com/?q=${location.latitude},${location.longitude} - Sent via Zenith App"
            } else {
                "HELP! I need assistance. Unable to fetch GPS location link - Sent via Zenith App"
            }
        } catch (e: SecurityException) {
            Log.e("ZenithSOS", "SecurityException fetching location data", e)
        } catch (e: Exception) {
            Log.e("ZenithSOS", "Exception fetching location data", e)
        }

        dispatchSmsToTrustedContacts(locationMessage)
    }

    private suspend fun dispatchSmsToTrustedContacts(message: String) {
        try {
            val phoneNumbers = sosContactsDao.getAllContactNumbers()
            if (phoneNumbers.isEmpty()) {
                Log.e("ZenithSOS", "No trusted contacts found in database.")
                return
            }

            if (ContextCompat.checkSelfPermission(
                    this@EmergencySosService,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("ZenithSOS", "SEND_SMS permission not granted.")
                return
            }

            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this@EmergencySosService.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            for (rawNumber in phoneNumbers) {
                val cleanNumber = rawNumber.replace(Regex("[^0-9+]"), "")
                if (cleanNumber.isNotBlank()) {
                    val parts = smsManager.divideMessage(message)
                    if (parts.size > 1) {
                        smsManager.sendMultipartTextMessage(cleanNumber, null, parts, null, null)
                    } else {
                        smsManager.sendTextMessage(cleanNumber, null, message, null, null)
                    }
                    Log.d("ZenithSOS", "Emergency message sent to $cleanNumber")
                } else {
                    Log.w("ZenithSOS", "Skipping empty or invalid contact number: $rawNumber")
                }
            }
            Log.d("ZenithSOS", "Emergency messages broadcasted successfully.")
        } catch (e: Exception) {
            Log.e("ZenithSOS", "Failed SMS dispatch transmission", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Emergency SOS Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used to dispatch emergency distress alerts"
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sending Emergency SOS")
            .setContentText("Dispatching location and distress message to trusted contacts...")
            .setSmallIcon(R.drawable.ic_zenith_shield)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    private suspend fun <T> Task<T>.awaitTask(): T? = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            if (cont.isActive) cont.resume(result)
        }
        addOnFailureListener {
            if (cont.isActive) cont.resume(null)
        }
        addOnCanceledListener {
            if (cont.isActive) cont.resume(null)
        }
    }
}
