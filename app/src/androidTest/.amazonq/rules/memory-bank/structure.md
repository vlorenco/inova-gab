# InovaGAB - Project Structure

## Directory Layout
```
InovaGAB/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/com/fiap/inovagab/
│   │   │   │   ├── MainActivity.kt          # Single entry-point Activity
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt         # Color palette definitions
│   │   │   │           ├── Theme.kt         # MaterialTheme composable wrapper
│   │   │   │           └── Type.kt          # Typography scale
│   │   │   ├── res/                         # Android resources (icons, drawables, values)
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/                     # Instrumented (on-device) tests
│   │   │   └── .amazonq/rules/memory-bank/  # Amazon Q memory bank (this directory)
│   │   └── test/                            # Unit tests (JVM)
│   ├── build.gradle.kts                     # App-level build config
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml                   # Version catalog (single source of truth for deps)
│   └── wrapper/
├── build.gradle.kts                         # Root build config
├── settings.gradle.kts                      # Module inclusion & repo config
└── gradle.properties
```

## Core Components

| Component | File | Responsibility |
|---|---|---|
| MainActivity | `MainActivity.kt` | App entry point, sets Compose content, enables edge-to-edge |
| InovaGABTheme | `ui/theme/Theme.kt` | Wraps MaterialTheme with project color/typography |
| Color palette | `ui/theme/Color.kt` | Defines light/dark color tokens |
| Typography | `ui/theme/Type.kt` | Defines text styles using Material 3 type scale |

## Architectural Patterns
- Single-Activity architecture — one `ComponentActivity`, all UI in Compose
- Unidirectional data flow ready — Compose state hoisting pattern expected as features grow
- Theme layer fully separated from screen logic (`ui/theme/` package)
- Version Catalog (`libs.versions.toml`) centralizes all dependency versions
