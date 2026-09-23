package sutanu.apps.zenith.data.repository_impl

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import sutanu.apps.zenith.data.local.db.dao.AlertHistoryDao
import sutanu.apps.zenith.data.local.db.dao.AppLimitDao
import sutanu.apps.zenith.data.local.db.entity.AppLimitEntity
import sutanu.apps.zenith.domain.model.AppInfo
import sutanu.apps.zenith.domain.repository.AppTimerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppTimerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appLimitDao: AppLimitDao,
    private val alertHistoryDao: AlertHistoryDao
) : AppTimerRepository {
    override fun getAppLimitsFlow(): Flow<List<AppLimitEntity>> = appLimitDao.getAllAppLimitsFlow()

    override suspend fun fetchInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager

        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(0)
        }

        apps.asSequence()
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || (it.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 }
            .map { AppInfo(it.packageName, it.loadLabel(packageManager).toString(), it.loadIcon(packageManager)) }
            .sortedBy { it.appName.lowercase() }
            .toList()
    }

    override suspend fun saveAppLimit(entity: AppLimitEntity) {
        alertHistoryDao.deleteAlertsForPackage(entity.packageName)
        appLimitDao.saveAppLimit(entity)
    }

    override suspend fun getAppLimitSync(packageName: String): AppLimitEntity? = appLimitDao.getAppLimit(packageName)

    override suspend fun updateDailyAppUsage(packageName: String, minutes: Int) = appLimitDao.updateDailyAppUsage(
        packageName = packageName,
        minutes = minutes
    )

    override suspend fun deleteAppLimit(packageName: String) {
        alertHistoryDao.deleteAlertsForPackage(packageName)
        appLimitDao.deleteAppLimit(packageName)
    }

    override suspend fun grantAppOverride(packageName: String, extensionMinutes: Int) {
        val expiration = System.currentTimeMillis() + (extensionMinutes * 60 * 1000L)
        appLimitDao.grantAppOverride(packageName, expiration)
    }
}
