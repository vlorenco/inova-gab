# InovaGAB - Development Guidelines

## Code Quality Standards

### Naming Conventions
- **Package**: lowercase, reverse-domain (`br.com.fiap.inovagab`, `br.com.fiap.inovagab.ui.theme`)
- **Classes/Objects**: PascalCase (`MainActivity`, `InovaGABTheme`)
- **Composable functions**: PascalCase, noun-like (`Greeting`, `GreetingPreview`, `InovaGABTheme`)
- **Top-level vals (theme tokens)**: PascalCase (`Purple80`, `LightColorScheme`, `Typography`)
- **Private vals**: camelCase with `private` modifier (`DarkColorScheme`, `LightColorScheme`)
- **Parameters**: camelCase (`darkTheme`, `dynamicColor`, `innerPadding`)

### File Organization
- One primary declaration per file, filename matches the main declaration
- Theme layer lives in `ui/theme/` and is split across 3 files: `Color.kt`, `Type.kt`, `Theme.kt`
- Screen/feature code lives directly under the root package or a dedicated feature sub-package

---

## Architectural Patterns

### Single-Activity + Jetpack Compose
All UI is rendered via Compose inside a single `ComponentActivity`. Never add a second Activity unless absolutely required by the platform (e.g., deep-link targets).

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InovaGABTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Screen content here
                }
            }
        }
    }
}
```

### Theme Wrapper Pattern
Always wrap screen content with `InovaGABTheme`. The theme handles dark/light mode and dynamic color automatically.

```kotlin
InovaGABTheme {
    // your composables
}
```

### Scaffold as Root Layout
Use `Scaffold` as the root composable for every screen to correctly handle system bar insets via `innerPadding`.

```kotlin
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Content(modifier = Modifier.padding(innerPadding))
}
```

### Edge-to-Edge
Always call `enableEdgeToEdge()` before `setContent {}` in every Activity to opt into full-screen rendering.

---

## Compose Patterns

### Composable Function Signature
- Stateless composables accept a `modifier: Modifier = Modifier` as the last (or near-last) parameter
- Pass `modifier` down to the root layout element of the composable

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
```

### Preview Annotation
Every public composable should have a paired `@Preview` composable:
- Annotate with `@Preview(showBackground = true)`
- Wrap with `InovaGABTheme` inside the preview
- Name: `<ComposableName>Preview`

```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InovaGABTheme {
        Greeting("Android")
    }
}
```

### Popular Annotations
| Annotation | Usage |
|---|---|
| `@Composable` | All UI functions |
| `@Preview` | Design-time previews, always with `showBackground = true` |

---

## Theme System

### Color Tokens (`Color.kt`)
Define colors as top-level `val` using `Color(0xFFRRGGBB)` hex literals. Use the `80`/`40` suffix convention for light/dark variants:

```kotlin
val Purple80 = Color(0xFFD0BCFF)  // dark theme primary
val Purple40 = Color(0xFF6650a4)  // light theme primary
```

### Color Scheme (`Theme.kt`)
- `LightColorScheme` uses `lightColorScheme(...)` with `40`-suffix tokens
- `DarkColorScheme` uses `darkColorScheme(...)` with `80`-suffix tokens
- Dynamic color (Material You) is enabled by default on Android 12+ (`Build.VERSION_CODES.S`)

### Typography (`Type.kt`)
- Define a single `Typography` val using `androidx.compose.material3.Typography`
- Use `TextStyle` with explicit `fontFamily`, `fontWeight`, `fontSize`, `lineHeight`, `letterSpacing`
- Keep commented-out style stubs for future overrides (project convention)

```kotlin
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

---

## Dependency Management

### Version Catalog
All versions and library coordinates live in `gradle/libs.versions.toml`. Never hardcode version strings in `build.gradle.kts`.

```toml
[versions]
kotlin = "2.0.21"

[libraries]
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }

[plugins]
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

Reference in build scripts as `libs.androidx.material3` / `libs.plugins.kotlin.compose`.

### Compose BOM
Always import the Compose BOM via `platform(libs.androidx.compose.bom)` so individual Compose library versions are managed centrally without explicit version pins.

---

## Android Manifest Conventions
- Single `<activity>` entry for `MainActivity` with `android:exported="true"`
- App theme set to `@style/Theme.InovaGAB` (XML theme used as window background before Compose renders)
- `android:supportsRtl="true"` always enabled
- Backup rules referenced via `android:dataExtractionRules` and `android:fullBackupContent`
