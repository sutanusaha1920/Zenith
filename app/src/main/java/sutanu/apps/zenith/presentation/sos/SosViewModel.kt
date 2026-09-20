package sutanu.apps.zenith.presentation.sos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.data.services.EmergencySosService
import sutanu.apps.zenith.domain.model.Sos
import sutanu.apps.zenith.domain.repository.SosRepository
import sutanu.apps.zenith.domain.usecase.sos.AddTrustedContactUseCase
import sutanu.apps.zenith.domain.usecase.sos.DeleteContactUseCase
import javax.inject.Inject

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
            context.startService(intent)
            Toast.makeText(context, "Distress alert triggered!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to trigger SOS: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
