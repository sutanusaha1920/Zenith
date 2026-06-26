package sutanu.apps.zenith.presentation.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sutanu.apps.zenith.data.local.db.entity.SosContactEntity
import sutanu.apps.zenith.domain.model.SosUiState
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

    private val _uiState = MutableStateFlow(SosUiState())
    val uiState: StateFlow<SosUiState> = _uiState.asStateFlow()

    init {
        observeTrustedContacts()
    }

    private fun observeTrustedContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingContacts = true)
            repository.getAllContactsFlow().collect { contactsList ->
                _uiState.value = _uiState.value.copy(
                    trustedContacts = contactsList,
                    isLoadingContacts = false
                )
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
        _uiState.value = _uiState.value.copy(showAddContactDialog = true)
    }

    fun dismissAddContactModal() {
        _uiState.value = _uiState.value.copy(showAddContactDialog = false)
    }

}