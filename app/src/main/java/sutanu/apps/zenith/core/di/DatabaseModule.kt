package sutanu.apps.zenith.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sutanu.apps.zenith.data.local.db.ZenithDatabase
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.dao.SosContactsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZenithDatabase {
        return ZenithDatabase.getDatabase(context)
    }

    @Provides
    fun provideAppLimitDao(database: ZenithDatabase): AppLimitDao {
        return database.appLimitDao()
    }

    @Provides
    fun provideSosContactsDao(database: ZenithDatabase): SosContactsDao {
        return database.sosContactsDao()
    }
}
