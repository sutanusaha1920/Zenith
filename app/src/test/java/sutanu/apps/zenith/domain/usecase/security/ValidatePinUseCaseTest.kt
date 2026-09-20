package sutanu.apps.zenith.domain.usecase.security

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sutanu.apps.zenith.core.util.SecurityUtils
import sutanu.apps.zenith.domain.repository.AuthRepository

class ValidatePinUseCaseTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var validatePinUseCase: ValidatePinUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        validatePinUseCase = ValidatePinUseCase(fakeRepository)
    }

    @Test
    fun `invoke with invalid pin length returns false`() = runBlocking {
        fakeRepository.setStoredPin("123456")
        assertFalse(validatePinUseCase("12345"))
        assertFalse(validatePinUseCase("1234567"))
    }

    @Test
    fun `invoke with empty stored pin returns false`() = runBlocking {
        fakeRepository.setStoredPin("")
        assertFalse(validatePinUseCase("123456"))
    }

    @Test
    fun `invoke with correct pin matching hash returns true`() = runBlocking {
        fakeRepository.setStoredPin("123456")
        assertTrue(validatePinUseCase("123456"))
    }

    @Test
    fun `invoke with incorrect pin returns false`() = runBlocking {
        fakeRepository.setStoredPin("123456")
        assertFalse(validatePinUseCase("000000"))
    }

    @Test
    fun `invoke with legacy unhashed 6-digit pin migrates and returns true`() = runBlocking {
        fakeRepository.setStoredRawLegacyPin("654321")
        assertTrue(validatePinUseCase("654321"))
        val expectedHash = SecurityUtils.hashPin("654321")
        assertTrue(fakeRepository.savedPinValue == expectedHash)
    }

    private class FakeAuthRepository : AuthRepository {
        private val pinFlow = MutableStateFlow("")
        var savedPinValue: String = ""

        fun setStoredPin(rawPin: String) {
            val hash = if (rawPin.isEmpty()) "" else SecurityUtils.hashPin(rawPin)
            savedPinValue = hash
            pinFlow.value = hash
        }

        fun setStoredRawLegacyPin(legacyPin: String) {
            savedPinValue = legacyPin
            pinFlow.value = legacyPin
        }

        override val parentalPin: Flow<String> = pinFlow

        override suspend fun savePin(pin: String) {
            val hash = SecurityUtils.hashPin(pin)
            savedPinValue = hash
            pinFlow.value = hash
        }

        override val isDeviceAdminActive: Flow<Boolean> = MutableStateFlow(true)
        override val isUninstallProtectionEnabled: Flow<Boolean> = MutableStateFlow(true)

        override suspend fun setUninstallProtection(enabled: Boolean) {}
    }
}
