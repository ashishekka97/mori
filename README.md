# Mori
[![Android CI](https://github.com/ashishekka/mori/actions/workflows/android.yml/badge.svg)](https://github.com/ashishekka/mori/actions)

A zero-allocation, privacy-first Live Wallpaper engine for Android.

Mori turns the device's home screen into a "living dashboard." Instead of static images or generic loops, it uses passive OS-level data (time, battery, health metrics) to dynamically render a time-shifting, handcrafted biome.

## Core Philosophy
1. **Zero Battery Drain:** The rendering engine (`WallpaperService`) strictly adheres to zero-allocation principles. No objects are instantiated inside the `drawFrame` loop. The thread kills itself immediately when the screen is locked or an app is opened.
2. **Absolute Privacy:** All "Persona" data (steps, screen time, battery) is processed entirely on-device. There are no backend tracking servers.
3. **Data as Art:** Raw metrics are obfuscated into atmospheric visual cues (e.g., a murky lake for high screen time, fireflies for low notifications).

## Tech Stack
* **Language:** Kotlin
* **Rendering:** Native Android `Canvas` API
* **Concurrency:** Kotlin Coroutines & `StateFlow`
* **Dependency Injection:** Koin
* **Background Sync:** Jetpack WorkManager
* **Local Caching:** Room Database
* **UI:** Jetpack Compose (for Settings/Onboarding)

## Project Structure
The app is packaged by feature into four distinct domains:
* `:ui` - Standard Android screens (Settings, Onboarding).
* `:persona` - Data aggregation (BroadcastReceivers, WorkManager, Room).
* `:biome` - Asset parsing (Decoding JSON configs and Bitmaps).
* `:engine` - The zero-allocation Canvas rendering loop.

## Development & CI/CD
* **Formatting:** Ktlint is strictly enforced. The styling rules are defined in the root `.editorconfig` file. Run `./gradlew ktlintFormat` before pushing.
* **CI Pipeline:** GitHub Actions automatically verifies the Koin dependency graph, runs tests, and builds the Debug APK on every push to `main`.
* **Testing:** We prioritize local JVM tests for business logic (`:persona`, `:biome`) using `app.cash.turbine`, `MockK`, and `koin-test`. We do not write automated UI tests for the Canvas rendering loop.