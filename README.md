# 💱 CurrencyApp

<div align="center">

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-%234285F4.svg?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)](https://developer.apple.com/ios/)

_A beautiful, modern currency exchange app built with Kotlin Multiplatform and Compose Multiplatform_

[Features](#-features) • [Screenshots](#-screenshots) • [Tech Stack](#-tech-stack) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [API](#-api)

</div>

---

## ✨ Features

### 🚀 Core Functionality

-   **Real-time Currency Exchange** - Get live exchange rates for 170+ currencies
-   **Smart Caching** - Intelligent local caching with freshness detection
-   **Offline Support** - Works offline with cached data
-   **Cross-Platform** - Single codebase for Android and iOS

### 🎨 User Experience

-   **Beautiful UI** - Modern Material Design 3 with smooth animations
-   **Intuitive Interface** - Easy currency selection with country flags
-   **Smart Search** - Quick currency search with real-time filtering
-   **Currency Switching** - One-tap currency pair switching
-   **Rate Status Indicator** - Visual indicators for fresh vs stale rates

### 🔧 Technical Features

-   **Automatic Updates** - Background rate updates with timestamp tracking
-   **Persistent Storage** - Local database with Realm
-   **Settings Persistence** - Remember user preferences
-   **Error Handling** - Graceful error handling with retry mechanisms

---

## 📱 Screenshots
<img src="https://github.com/user-attachments/assets/bc1daa6d-35d2-4746-a211-fa5200e8fb47" width="200" height="400">
<img src="https://github.com/user-attachments/assets/d563b460-273b-4458-8267-f68af3f50995" width="200" height="400">
<img src="https://github.com/user-attachments/assets/fb457f98-3951-4784-910d-8c87c61c9706" width="200" height="400"><br />
<img src="https://github.com/user-attachments/assets/42057c62-4cdd-4f61-bd70-291047eecc92" width="200" height="400">

<img src="https://github.com/user-attachments/assets/1d89284e-1412-45b7-b197-d5fb1bba8d7f" width="200" height="400">

---

## 🛠 Tech Stack

### **Frontend**

-   **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)** - Share business logic across platforms
-   **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** - Declarative UI framework
-   **[Material Design 3](https://m3.material.io/)** - Modern design system
-   **[Voyager](https://voyager.adriel.cafe/)** - Navigation library for Compose Multiplatform

### **Networking & Data**

-   **[Ktor Client](https://ktor.io/docs/client.html)** - HTTP client for API calls
-   **[Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)** - JSON serialization
-   **[Realm](https://realm.io/realm-kotlin/)** - Local database for caching
-   **[Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)** - Shared preferences

### **Architecture & DI**

-   **[Koin](https://insert-koin.io/)** - Dependency injection
-   **[Kotlinx Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming
-   **[Kotlinx DateTime](https://kotlinlang.org/api/kotlinx-datetime/)** - Date and time handling
-   **MVVM Pattern** - Clean architecture with ViewModels

---

## 🏗 Architecture

```
CurrencyApp/
├── composeApp/
│   ├── commonMain/          # Shared code for all platforms
│   │   ├── data/           # Data layer
│   │   │   ├── local/      # Local storage (Realm, Preferences)
│   │   │   └── remote/     # Network layer (Ktor, API)
│   │   ├── domain/         # Business logic
│   │   │   └── model/      # Domain models
│   │   ├── presentation/   # UI layer
│   │   │   ├── component/  # Reusable UI components
│   │   │   └── screens/    # Screen composables & ViewModels
│   │   ├── di/            # Dependency injection
│   │   └── util/          # Utility functions
│   ├── androidMain/        # Android-specific code
│   └── iosMain/           # iOS-specific code
└── iosApp/                # iOS app entry point
```

### **Key Components**

-   **Data Layer**: Repository pattern with local caching using Realm
-   **Domain Layer**: Use cases and business logic
-   **Presentation Layer**: Compose UI with MVVM architecture
-   **Dependency Injection**: Koin modules for clean dependency management

---

## 🚀 Getting Started

### **Prerequisites**

-   **JDK 11** or higher
-   **Android Studio** with Kotlin Multiplatform plugin
-   **Xcode 14+** (for iOS development)
-   **CocoaPods** (for iOS dependencies)

### **Installation**

1. **Clone the repository**

    ```bash
    git clone https://github.com/yourusername/CurrencyApp.git
    cd CurrencyApp
    ```

2. **Get your API key**

    - Sign up at [CurrencyAPI](https://currencyapi.com/)
    - Replace `API_KEY` in `CurrencyApiServiceImpl.kt` with your key

3. **Build and run**

    **For Android:**

    ```bash
    ./gradlew :composeApp:installDebug
    ```

    **For iOS:**

    ```bash
    cd iosApp
    pod install
    open iosApp.xcworkspace
    ```

### **Configuration**

The app uses [CurrencyAPI](https://currencyapi.com/) for real-time exchange rates. Make sure to:

-   Sign up for a free account
-   Get your API key
-   Replace the placeholder in the source code

---

## 🌐 API

The app integrates with **CurrencyAPI** to fetch real-time exchange rates for 170+ currencies.

**Features:**

-   Real-time exchange rates
-   Historical data support
-   99.9% uptime guarantee
-   Multiple base currency support

**Rate Limiting:**

-   Free tier: 300 requests/month
-   Paid tiers available for higher usage

---

## 📂 Project Structure

### **Shared Code (`commonMain`)**

```kotlin
// Domain Models
data class Currency(
    val code: String,
    val value: Double
)

// API Service
interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<Currency>>
}

// Repository Pattern
interface PreferenceRepository {
    suspend fun saveSourceCurrencyCode(code: String)
    suspend fun saveTargetCurrencyCode(code: String)
    fun readSourceCurrencyCode(): Flow<CurrencyCode>
    fun readTargetCurrencyCode(): Flow<CurrencyCode>
}
```

### **UI Components**

-   **HomeScreen**: Main currency conversion interface
-   **CurrencyPickerDialog**: Currency selection with search
-   **HomeHeader**: Rate status and currency inputs
-   **HomeBody**: Conversion results and actions

---

## 🎯 Key Features in Detail

### **Real-time Exchange Rates**

-   Fetches live rates from CurrencyAPI
-   Supports 170+ currencies with country flags
-   Smart caching to minimize API calls

### **Offline Support**

-   Local storage with Realm database
-   Automatic cache refresh when data becomes stale
-   Graceful fallback to cached data

### **User Experience**

-   Smooth animations and transitions
-   Material Design 3 theming
-   Intuitive currency selection
-   Visual rate freshness indicators

### **Performance**

-   Efficient data caching strategy
-   Background updates
-   Optimized for battery life

---

<div align="center">

**Built with ❤️ using Kotlin Multiplatform & Compose Multiplatform**

⭐ **Star this repo if you found it helpful!** ⭐

</div>in Multiplatform project targeting Android, iOS.

-   `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
    It contains several subfolders:

    -   `commonMain` is for code that’s common for all targets.
    -   Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
        For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
        `iosMain` would be the right folder for such calls.

-   `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
    you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
