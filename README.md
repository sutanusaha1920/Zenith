# 🌌 Zenith — Screen Time Safeguard & Digital Wellbeing

**Zenith** is an advanced, modern Android Screen Time Safeguard, Digital Wellbeing, and Parental Control application built natively in Kotlin using Jetpack Compose, Material 3, Clean Architecture, Room Database, and Hilt Dependency Injection.

---

## ✨ Key Features

### 📊 1. Home Dashboard
* **Real-time Screen Time Overview**: View total device usage today and total monitored applications.
* **Limit Status Indicator**: Dynamic "Screen time today" card indicating normal or limit exceeded status.
* **Hybrid Alert Feed**: Displays real-time and historical limit warnings (`Approaching` at 80% limit, `Exceeded` at 100% limit).

### ⏱️ 2. Screen Timer & Limit Controls
* **Master Power Switch**: Single toggle at the top of Screen Timer to master turn ON/OFF all limit enforcement across Zenith.
* **Entire Device Screen Time**: Set daily total device usage limits (1h–12h) with a sleek animated circular arc progress gauge.
* **Individual App Limits**: Configure custom daily limits for specific installed applications (0.5h–6h) with live search filtering.
* **PIN Override Extensions**: When a limit is hit, entering the parental authorization PIN on the lock screen grants a temporary extension before re-enforcing limits:
  * **Device Extension Intervals**: 10m, 20m, 30m (default), 40m, 50m, 1h.
  * **App Extension Intervals**: 5m, 10m, 15m (default), 20m, 25m, 30m.

### 🌙 3. Bedtime Mode
* **Overnight Schedule Windows**: Configure bedtime start and end times (e.g., 10:00 PM to 07:00 AM).
* **Selective Exception Toggles**: Allow essential system applications during bedtime hours:
  * 📞 Phone Calls / Dialer
  * ⏰ Alarm Clocks
  * 📶 Wi-Fi & Network Settings
* **Automatic Screen Lock & Auto-Unlock**: Blocks non-exempt apps during bedtime hours and automatically dismisses the overlay when bedtime ends.

### 🚨 4. Offline Emergency SOS & Location
* **3× Volume Up Hardware Button Trigger**: Pressing the Volume Up key 3 times consecutively within 2.5 seconds triggers the emergency SOS flow system-wide.
* **Offline GPS Location Link**: Fetches real-time GPS coordinates and builds a Google Maps location link without needing active mobile internet.
* **Offline Cellular SMS Broadcasting**: Automatically dispatches distress messages to all trusted contacts via standard cellular SMS.

### 🛡️ 5. App Deletion Protection (Accessibility Shield)
* **Uninstall & Settings Interception**: Replaces traditional Device Policy Manager (DPM) with Accessibility node hierarchy inspection (`AccessibilityMonitor`).
* **Zero False Positives**: Differentiates between uninstall/force-stop attempts and user management of Accessibility permissions in system Settings.
* **PIN Protected Toggle**: Navigates directly to `DeletionProtectionScreen` requiring 6-digit PIN verification to disable.

### 🔔 6. Heads-Up System Notifications
* **Approaching Limit Warnings**: Dispatches a high-priority heads-up system notification with sound and vibration when an app reaches **80%** of its set daily limit (`⚠️ Approaching Time Limit`).
* **Limit Reached Warnings**: Notifies the user when **100%** limit is reached (`🚫 Daily Time Limit Reached`).

### 🎨 7. Dynamic Palette & Theme System
* **Light & Dark Theme Switcher**: Toggle between Dark, Light, or System Default theme under Settings.
* **Theme Reactivity**: Custom Compose color delegation (`Color.kt`) reacting dynamically via `LocalDarkTheme` while preserving original dark theme variables and hex definitions.

---

## 🛠️ Architecture & Tech Stack

Zenith follows Google's recommended **Clean Architecture** with a unidirectional data flow (UDF) pattern.

```
sutanu.apps.zenith
├── core                # DI Modules (Hilt), Helpers, Security & Bedtime Utilities
├── data                # Local Room Database, DataStore Preferences, Services, Repositories
│   ├── local/db        # Room Entities & DAOs (App limits, SOS contacts, Alert history)
│   ├── preferences     # DataStore AuthPreferences (PIN hash, theme, bedtime, extensions)
│   └── services        # AccessibilityMonitor, UsageSyncService, EmergencySosService, HardwareButtonReceiver
├── domain              # Data Models, Repository Interfaces, & Business UseCases
└── presentation        # Jetpack Compose UI Screens, ViewModels, Theme, & Navigation
```

* **UI Framework**: Jetpack Compose with Material 3 (M3)
* **Architecture Pattern**: MVVM + Clean Architecture UseCases
* **Dependency Injection**: Hilt (Dagger)
* **Local Persistence**: Jetpack DataStore (Preferences) & Room SQLite Database
* **Asynchronous Programming**: Kotlin Coroutines & StateFlow
* **Background Monitoring**:
  * `AccessibilityService` (`AccessibilityMonitor`) for real-time app launches, uninstall interception, and 3× Volume Up key listening.
  * `ForegroundService` (`UsageSyncService`) for periodic usage quota background polling.
  * `LifecycleService` (`EmergencySosService`) for background location fetching and SMS dispatching.

---

## 📋 Prerequisites & Required Permissions

| Permission | Purpose |
| :--- | :--- |
| `BIND_ACCESSIBILITY_SERVICE` | Real-time app launch detection, uninstall protection, and Volume Up key trigger |
| `PACKAGE_USAGE_STATS` | Measuring daily screen time usage per app and device |
| `ACCESS_FINE_LOCATION` | Fetching GPS coordinates for Emergency SOS alerts |
| `SEND_SMS` | Transmitting distress SMS messages to trusted contacts |
| `POST_NOTIFICATIONS` | Displaying approaching time limit heads-up system notifications |
| `SYSTEM_ALERT_WINDOW` / Translucent Overlay | Rendering full-screen lock screen barriers over restricted apps |

---

## 🚀 Building & Running

1. **Clone Repository**:
   ```bash
   git clone https://github.com/your-username/Zenith.git
   cd Zenith
   ```
2. **Open in Android Studio**:
   Open the project folder in Android Studio (Ladybug / Jellyfish or later).
3. **Gradle Build**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Deploy to Device**:
   Run on a physical Android device or emulator running **Android 10 (API 29)** or higher.

---

## 🔒 Security Considerations
* **Hashed PIN**: Parental authorization PIN is cryptographically hashed using SHA-256 before DataStore storage.
* **Backstack Cleared**: Lock operations clear navigation backstacks (`popUpTo(0)`) and handle hardware back presses with `BackHandler` app minimization to prevent PIN bypasses.

---

## 📄 License
Designed & Developed by **SUTANU SAHA**. All rights reserved.
