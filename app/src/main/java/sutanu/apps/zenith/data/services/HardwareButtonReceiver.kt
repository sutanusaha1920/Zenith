package sutanu.apps.zenith.data.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import kotlin.jvm.java

import androidx.core.content.ContextCompat

class HardwareButtonReceiver : BroadcastReceiver() {

    companion object{
        private var clickCount = 0
        private var lastClickTime: Long = 0
        private const val CLICK_DELAY_MAX = 2500 // window for 3 times power button click
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_ON || intent.action == Intent.ACTION_SCREEN_OFF) {
            val currentTime = SystemClock.elapsedRealtime()

            if (currentTime - lastClickTime > CLICK_DELAY_MAX) {
                clickCount = 0
            }

            if (clickCount == 0) {
                lastClickTime = currentTime
            }

            clickCount++
            Log.d("ZenithHardware", "Power/Screen click recognised count: $clickCount")

            // On 3rd consecutive click, perform SOS action
            if (clickCount >= 3) {
                clickCount = 0
                Log.d("ZenithHardware", "SOS action triggered")

                triggerEmergencyService(context)
            }
        }
    }

    private fun triggerEmergencyService(context: Context) {
        val serviceIntent = Intent(context, EmergencySosService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}