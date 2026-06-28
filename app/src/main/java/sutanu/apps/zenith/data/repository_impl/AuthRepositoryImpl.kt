package sutanu.apps.zenith.data.repository_impl

import kotlinx.coroutines.flow.Flow
import sutanu.apps.zenith.data.local.preferences.AuthPreferences
import sutanu.apps.zenith.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override val parentalPin: Flow<String> = authPreferences.parentalPin

    override suspend fun savePin(pin: String) {
        authPreferences.savePin(pin)
    }

}