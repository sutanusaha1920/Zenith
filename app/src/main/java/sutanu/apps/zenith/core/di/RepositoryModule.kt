package sutanu.apps.zenith.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sutanu.apps.zenith.data.repository_impl.AuthRepositoryImpl
import sutanu.apps.zenith.data.repository_impl.BedtimeRepositoryImpl
import sutanu.apps.zenith.data.repository_impl.SosRepositoryImpl
import sutanu.apps.zenith.domain.repository.AuthRepository
import sutanu.apps.zenith.domain.repository.BedtimeRepository
import sutanu.apps.zenith.domain.repository.SosRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSosRepository(
        sosRepositoryImpl: SosRepositoryImpl
    ): SosRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBedtimeRepository(
        bedtimeRepositoryImpl: BedtimeRepositoryImpl
    ): BedtimeRepository
}