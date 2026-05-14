# InovaGAB - Technology Stack

## Languages & Versions
- Kotlin 2.0.21
- Java compatibility: VERSION_11 (source & target), JVM target 11

## Android SDK
- compileSdk: 36
- minSdk: 28 (Android 9 Pie+)
- targetSdk: 36

## Build System
- Gradle with Kotlin DSL (`build.gradle.kts`)
- Android Gradle Plugin (AGP): 8.10.0
- Gradle Wrapper: 8.11.1
- Version Catalog: `gradle/libs.versions.toml`

## Core Dependencies

| Library | Version | Purpose |
|---|---|---|
| androidx.core:core-ktx | 1.17.0 | Kotlin extensions for Android core APIs |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.10.0 | Lifecycle-aware coroutine scopes |
| androidx.activity:activity-compose | 1.12.4 | `setContent {}` and `ComponentActivity` integration |
| androidx.compose:compose-bom | 2024.09.00 | BOM for consistent Compose library versions |
| androidx.compose.ui:ui | (BOM) | Core Compose UI |
| androidx.compose.ui:ui-graphics | (BOM) | Graphics primitives |
| androidx.compose.material3:material3 | (BOM) | Material Design 3 components |
| androidx.compose.ui:ui-tooling-preview | (BOM) | `@Preview` support |

## Test Dependencies
| Library | Version | Purpose |
|---|---|---|
| junit:junit | 4.13.2 | JVM unit tests |
| androidx.test.ext:junit | 1.3.0 | AndroidX JUnit runner |
| androidx.test.espresso:espresso-core | 3.7.0 | UI instrumentation tests |
| androidx.compose.ui:ui-test-junit4 | (BOM) | Compose UI testing |

## Build Features
- `compose = true` — enables Compose compiler plugin
- Kotlin Compose compiler plugin: `org.jetbrains.kotlin.plugin.compose` 2.0.21
- ProGuard: configured but minification disabled in release (`isMinifyEnabled = false`)

## Key Build Commands
```bash
# Assemble debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Build release APK
./gradlew assembleRelease
```

## Package / Application ID
- `br.com.fiap.inovagab`
- Version: 1.0 (versionCode 1)
