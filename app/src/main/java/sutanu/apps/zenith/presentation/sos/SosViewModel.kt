package sutanu.apps.zenith.presentation.sos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.data.services.EmergencySosService
import sutanu.apps.zenith.domain.model.Sos
import sutanu.apps.zenith.domain.repository.SosRepository
import sutanu.apps.zenith.domain.usecase.sos.AddTrustedContactUseCase
import sutanu.apps.zenith.domain.usecase.sos.DeleteContactUseCase
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class SosViewModel @Inject constructor(
    private val repository: SosRepository,
    private val addTrustedContactUseCase: AddTrustedContactUseCase,
    private val deleteContactUseCase: DeleteContactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(Sos())
    val uiState: StateFlow<Sos> = _uiState.asStateFlow()

    init {
        observeTrustedContacts()
    }

    private fun observeTrustedContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingContacts = true) }
            repository.getAllContactsFlow().collect { contactsList ->
                _uiState.update {
                    it.copy(
                        trustedContacts = contactsList,
                        isLoadingContacts = false
                    )
                }
            }
        }
    }

    fun fetchRealTimeLocation(context: Context) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            _uiState.update {
                it.copy(
                    lastKnownLocation = "Location permission required",
                    gpsAccuracy = "Grant location permission to view live map",
                    isLoadingLocation = false
                )
            }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoadingLocation = true) }
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                // 1. Instant check via cached lastLocation
                val cachedLocation: Location? = try {
                    fusedLocationClient.lastLocation.awaitTask()
                } catch (_: Exception) {
                    null
                }

                if (cachedLocation != null) {
                    val addressName = reverseGeocode(context, cachedLocation.latitude, cachedLocation.longitude)
                    val accuracyString = "Accurate to within ${cachedLocation.accuracy.toInt()}m"

                    _uiState.update {
                        it.copy(
                            latitude = cachedLocation.latitude,
                            longitude = cachedLocation.longitude,
                            lastKnownLocation = addressName,
                            gpsAccuracy = accuracyString,
                            isLoadingLocation = false
                        )
                    }
                }

                // 2. Fresh GPS high-accuracy fix
                val cts = CancellationTokenSource()
                val freshLocation: Location? = withTimeoutOrNull(6000) {
                    try {
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cts.token
                        ).awaitTask()
                    } catch (_: Exception) {
                        null
                    }
                }

                if (freshLocation != null) {
                    val addressName = reverseGeocode(context, freshLocation.latitude, freshLocation.longitude)
                    val accuracyString = "Accurate to within ${freshLocation.accuracy.toInt()}m"

                    _uiState.update {
                        it.copy(
                            latitude = freshLocation.latitude,
                            longitude = freshLocation.longitude,
                            lastKnownLocation = addressName,
                            gpsAccuracy = accuracyString,
                            isLoadingLocation = false
                        )
                    }
                } else if (cachedLocation == null) {
                    _uiState.update {
                        it.copy(
                            lastKnownLocation = "GPS signal weak",
                            gpsAccuracy = "Ensure Location/GPS is enabled on device",
                            isLoadingLocation = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("SosViewModel", "Failed fetching real-time location", e)
                _uiState.update {
                    it.copy(
                        lastKnownLocation = "Location unavailable",
                        gpsAccuracy = e.localizedMessage ?: "Error fetching location",
                        isLoadingLocation = false
                    )
                }
            }
        }
    }

    private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): String = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val formattedCoords = "${String.format(Locale.US, "%.4f", lat)}° N, ${String.format(Locale.US, "%.4f", lng)}° E"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var resultAddress = formattedCoords
                suspendCancellableCoroutine<Unit> { cont ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val addr = addresses[0]
                            val feature = addr.subLocality ?: addr.featureName ?: ""
                            val locality = addr.locality ?: addr.subAdminArea ?: ""
                            val country = addr.countryName ?: ""
                            val formatted = listOf(feature, locality, country).filter { it.isNotBlank() }.joinToString(", ")
                            if (formatted.isNotBlank()) {
                                resultAddress = formatted
                            }
                        }
                        if (cont.isActive) cont.resume(Unit)
                    }
                }
                resultAddress
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val feature = addr.subLocality ?: addr.featureName ?: ""
                    val locality = addr.locality ?: addr.subAdminArea ?: ""
                    val country = addr.countryName ?: ""
                    val formatted = listOf(feature, locality, country).filter { it.isNotBlank() }.joinToString(", ")
                    if (formatted.isNotBlank()) formatted else formattedCoords
                } else {
                    formattedCoords
                }
            }
        } catch (e: Exception) {
            "${String.format(Locale.US, "%.4f", lat)}° N, ${String.format(Locale.US, "%.4f", lng)}° E"
        }
    }

    private suspend fun <T> Task<T>.awaitTask(): T? = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> if (cont.isActive) cont.resume(result) }
        addOnFailureListener { if (cont.isActive) cont.resume(null) }
        addOnCanceledListener { if (cont.isActive) cont.resume(null) }
    }

    fun addNewContact(name: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addTrustedContactUseCase(name, phone)
            dismissAddContactModal()
        }
    }

    fun removeContact(contact: SosContactEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteContactUseCase(contact)
        }
    }

    fun showAddContactModal() {
        _uiState.update { it.copy(showAddContactDialog = true) }
    }

    fun dismissAddContactModal() {
        _uiState.update { it.copy(showAddContactDialog = false) }
    }

    fun triggerEmergencySos(context: Context) {
        val hasSmsPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasSmsPermission) {
            Toast.makeText(
                context,
                "SMS Permission required to send SOS alert",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (_uiState.value.trustedContacts.isEmpty()) {
            Toast.makeText(
                context,
                "Please add at least one trusted contact first",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val intent = Intent(context, EmergencySosService::class.java)
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context, "Distress alert triggered!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to trigger SOS: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
