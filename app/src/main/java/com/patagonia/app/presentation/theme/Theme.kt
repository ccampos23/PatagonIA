package com.patagonia.app.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnErrorWhite,
    primaryContainer = PrimaryGreenLight,
    secondary = SecondaryBrown,
    onSecondary = OnErrorWhite,
    secondaryContainer = SecondaryBrownLight,
    tertiary = TertiaryBlue,
    onTertiary = OnErrorWhite,
    tertiaryContainer = TertiaryBlueLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = ErrorRed,
    onError = OnErrorWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = PrimaryGreenDark,
    primaryContainer = PrimaryGreen,
    secondary = SecondaryBrownLight,
    onSecondary = SecondaryBrownDark,
    secondaryContainer = SecondaryBrown,
    tertiary = TertiaryBlueLight,
    onTertiary = TertiaryBlueDark,
    tertiaryContainer = TertiaryBlue,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = ErrorRed,
    onError = OnErrorWhite,
)

@Composable
fun PatagoniaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
