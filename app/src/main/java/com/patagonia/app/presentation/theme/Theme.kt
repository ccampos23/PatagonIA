package com.patagonia.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colors Material 3 has no slot for: success + warning (+ info, aliased to tertiary).
 * Provided alongside the M3 ColorScheme so screens read both via [MaterialTheme] extensions.
 */
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val info: Color,
    val onInfo: Color,
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        success = Color.Black,
        onSuccess = Color.Black,
        warning = Color.Black,
        onWarning = Color.Black,
        info = Color.Black,
        onInfo = Color.Black,
    )
}

/** Access success / warning / info the same way as [MaterialTheme.colorScheme]. */
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current

private fun PatagoniaPalette.toLightColorScheme(): ColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = primaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = primary,
    outline = outline,
    outlineVariant = outlineVariant,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
)

private fun PatagoniaPalette.toDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = primaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = primary,
    outline = outline,
    outlineVariant = outlineVariant,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
)

private fun PatagoniaPalette.toExtended(): ExtendedColors = ExtendedColors(
    success = success,
    onSuccess = onSuccess,
    warning = warning,
    onWarning = onWarning,
    info = tertiary,
    onInfo = onTertiary,
)

/**
 * Root app theme. Brand is consistent across devices: Material You dynamic color is OFF by
 * default (PRODUCT.md: marca consistente). Follows system light/dark for browse surfaces.
 */
@Composable
fun PatagoniaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) PatagoniaDarkPalette else PatagoniaLightPalette
    val colorScheme = if (darkTheme) palette.toDarkColorScheme() else palette.toLightColorScheme()
    CompositionLocalProvider(LocalExtendedColors provides palette.toExtended()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PatagoniaTypography,
            shapes = PatagoniaShapes,
            content = content,
        )
    }
}

/**
 * Forced-dark theme for the capture flow (camera / viewfinder / review / first-run loading).
 * Camera chrome must be dark regardless of system setting — like every native camera app —
 * so the viewfinder reads and the brand's dark forest surface stays consistent.
 */
@Composable
fun CaptureTheme(content: @Composable () -> Unit) {
    val palette = PatagoniaDarkPalette
    CompositionLocalProvider(LocalExtendedColors provides palette.toExtended()) {
        MaterialTheme(
            colorScheme = palette.toDarkColorScheme(),
            typography = PatagoniaTypography,
            shapes = PatagoniaShapes,
            content = content,
        )
    }
}
