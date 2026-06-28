package sutanu.apps.zenith.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val parentalPin: Flow<String>

    suspend fun savePin(pin: String)
}