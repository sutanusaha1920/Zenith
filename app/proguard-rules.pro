# Preserve Line Numbers for Crash Reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room DB Entities, DAOs & Database
-keep class * extends androidx.room.RoomDatabase
-keep class sutanu.apps.zenith.data.local.db.entity.** { *; }
-keep class sutanu.apps.zenith.data.local.db.dao.** { *; }
-dontwarn androidx.room.paging.**

# Dagger / Hilt
-keep class * extends dagger.hilt.internal.UnsafeCasts { *; }
-keep class dagger.hilt.android.internal.managers.** { *; }
-keepclassmembers,allowobfuscation class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Jetpack DataStore & Preferences
-keep class androidx.datastore.preferences.protobuf.** { *; }

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**
