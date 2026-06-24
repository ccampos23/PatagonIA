package com.patagonia.app.presentation.theme

import androidx.compose.ui.graphics.Color

/*
 * Patagonia color system.
 *
 * Strategy: Restrained (product register). One brand hue — forest green — carries primary
 * actions and brand accents. Neutrals are tinted toward that same green hue (not toward a
 * generic warm/cool default), and every semantic role (success / warning / error / info) has
 * one and only one value per mode. OKLCH is the design intent; values land as sRGB hex for
 * Compose (OKLCH noted in comments where it clarifies the lightness/chroma decision).
 *
 * Modes:
 *   - Light palette is the primary surface for browse / map / management (bright Patagonian
 *     sunlight, high-contrast ink).
 *   - Dark palette is forced for the capture flow (camera / viewfinder / review) via
 *     CaptureTheme — a dark camera chrome is correct UX, not a system-dark-mode dependency.
 *
 * Contrast is pinned by ColorContrastTest. Do not loosen those thresholds.
 */

// ── Forest brand ramp (hue ~150–155 OKLCH) ────────────────────────────────────
val Forest0 = Color(0xFF07140E) // near-black green — dark background
val Forest1 = Color(0xFF0F211A) // dark surface
val Forest2 = Color(0xFF163024) // dark surface variant
val Forest3 = Color(0xFF1B4332) // dark primary container / earthy mid
val Forest4 = Color(0xFF2E6A4F) // deep forest — light primary (white text clears AA)
val Forest6 = Color(0xFF5FBF8A) // bright forest — dark primary (dark text clears AA)
val Forest8 = Color(0xFFC9EBD4) // mint tint — light primary container
val MintText = Color(0xFFE6F3EA) // bright mint-white — dark onSurface
val MintVariant = Color(0xFFA8C7B6) // muted mint — dark onSurfaceVariant
val MintChipText = Color(0xFF0C3320) // deep green text on mint container

// ── Neutrals (tinted toward green hue, not warm/cool default) ─────────────────
val Ink = Color(0xFF14241B) // primary ink — light onSurface/onBackground
val InkVariant = Color(0xFF3E5A4A) // secondary ink — light onSurfaceVariant (replaces Color.Gray)
val OutlineLight = Color(0xFF5C7565)
val OutlineVariantLight = Color(0xFFB4C4B8)
val OutlineDark = Color(0xFF4A8268) // light enough to clear 3:1 against the near-black dark bg
val BgLight = Color(0xFFF4F8F4) // near-white, faint green tint — avoids warm beige default
val SurfaceVariantLight = Color(0xFFE3EAE3)
val White = Color(0xFFFFFFFF)

// ── Earth (secondary — trail / ground) ────────────────────────────────────────
val Earth4 = Color(0xFF5D4037)
val Earth8 = Color(0xFFEADBD0)
val EarthDarkText = Color(0xFF2E1B12)
val EarthLight = Color(0xFFB89A8E)
val EarthLightText = Color(0xFF1A0E08)

// ── Teal (tertiary / info) ────────────────────────────────────────────────────
val Teal5 = Color(0xFF2D5F66)
val Teal6 = Color(0xFF7FD3DE)
val TealDarkText = Color(0xFF06262B)

// ── Semantic ──────────────────────────────────────────────────────────────────
// Error: ONE red across the app (was previously split #B3261E vs #E63946).
val RedLight = Color(0xFFB3261E)
val RedLightContainer = Color(0xFFF9D9D6)
val RedLightContainerText = Color(0xFF410005)
val RedDark = Color(0xFFFFB4AB)
val RedDarkContainer = Color(0xFF93000A)

val SuccessLight = Color(0xFF217A43)
val SuccessDark = Color(0xFF7BD99A)
val SuccessDarkText = Color(0xFF00390E)

// Warning amber: dark enough for white text on light; bright variant for dark mode.
val AmberLight = Color(0xFF8A5D00)
val AmberDark = Color(0xFFFFC069)
val AmberDarkText = Color(0xFF3A2400)

/**
 * Full token set for one mode. Mirrors the Material 3 ColorScheme roles the app consumes,
 * plus semantic success / warning (M3 has no slots for those). The same data feeds both the
 * M3 ColorScheme and the [ExtendedColors] CompositionLocal, so a palette swap (light/dark)
 * stays in one place.
 */
data class PatagoniaPalette(
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
)

val PatagoniaLightPalette = PatagoniaPalette(
    background = BgLight,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkVariant,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    primary = Forest4,
    onPrimary = White,
    primaryContainer = Forest8,
    onPrimaryContainer = MintChipText,
    secondary = Earth4,
    onSecondary = White,
    secondaryContainer = Earth8,
    onSecondaryContainer = EarthDarkText,
    tertiary = Teal5,
    onTertiary = White,
    error = RedLight,
    onError = White,
    errorContainer = RedLightContainer,
    onErrorContainer = RedLightContainerText,
    success = SuccessLight,
    onSuccess = White,
    warning = AmberLight,
    onWarning = White,
)

val PatagoniaDarkPalette = PatagoniaPalette(
    background = Forest0,
    onBackground = MintText,
    surface = Forest1,
    onSurface = MintText,
    surfaceVariant = Forest2,
    onSurfaceVariant = MintVariant,
    outline = OutlineDark,
    outlineVariant = Forest2,
    primary = Forest6,
    onPrimary = Forest0,
    primaryContainer = Forest3,
    onPrimaryContainer = Forest8,
    secondary = EarthLight,
    onSecondary = EarthLightText,
    secondaryContainer = Forest3,
    onSecondaryContainer = Forest8,
    tertiary = Teal6,
    onTertiary = TealDarkText,
    error = RedDark,
    onError = Color(0xFF690005),
    errorContainer = RedDarkContainer,
    onErrorContainer = RedDark,
    success = SuccessDark,
    onSuccess = SuccessDarkText,
    warning = AmberDark,
    onWarning = AmberDarkText,
)
