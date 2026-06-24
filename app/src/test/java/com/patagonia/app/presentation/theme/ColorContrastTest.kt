package com.patagonia.app.presentation.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Pins the Patagonia palette to WCAG contrast thresholds.
 *
 * PRODUCT.md declares "alto contraste legible en exteriores" the top accessibility priority.
 * These tests guard the token values so a future tweak cannot silently drop a text/background
 * pair below AA. Regression here = a real outdoor-readability regression.
 *
 * Thresholds:
 *   - body text / content on surface ........ 4.5:1 (WCAG AA)
 *   - UI components (outlines, borders) ...... 3.0:1 (WCAG AA)
 */
class ColorContrastTest {

    private fun linearize(channel: Float): Float =
        if (channel <= 0.03928f) channel / 12.92f
        else ((channel + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()

    private fun luminance(color: Color): Float {
        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }

    /** WCAG contrast ratio between two colors, as the larger of the two orderings. */
    fun contrast(fg: Color, bg: Color): Double {
        val fgL = luminance(fg)
        val bgL = luminance(bg)
        val lighter = max(fgL, bgL)
        val darker = min(fgL, bgL)
        return ((lighter + 0.05) / (darker + 0.05)).toDouble()
    }

    private fun assertBody(label: String, fg: Color, bg: Color) {
        val c = contrast(fg, bg)
        assertTrue("$label: $c:.2 < 4.5 (AA body)".replace(":.2", " %.2f".format(c)), c >= 4.5)
    }

    private fun assertUi(label: String, fg: Color, bg: Color) {
        val c = contrast(fg, bg)
        assertTrue("$label: $c:.2 < 3.0 (UI)".replace(":.2", " %.2f".format(c)), c >= 3.0)
    }

    private fun assertPaletteBody(name: String, p: PatagoniaPalette) {
        assertBody("$name onSurface/background", p.onSurface, p.background)
        assertBody("$name onBackground/background", p.onBackground, p.background)
        assertBody("$name onSurfaceVariant/background", p.onSurfaceVariant, p.background)
        assertBody("$name onSurface/surface", p.onSurface, p.surface)
        assertBody("$name onSurfaceVariant/surface", p.onSurfaceVariant, p.surface)
        assertBody("$name onPrimary/primary", p.onPrimary, p.primary)
        assertBody("$name onPrimaryContainer/primaryContainer", p.onPrimaryContainer, p.primaryContainer)
        assertBody("$name onSecondary/secondary", p.onSecondary, p.secondary)
        assertBody("$name onSecondaryContainer/secondaryContainer", p.onSecondaryContainer, p.secondaryContainer)
        assertBody("$name onTertiary/tertiary", p.onTertiary, p.tertiary)
        assertBody("$name onError/error", p.onError, p.error)
        assertBody("$name onErrorContainer/errorContainer", p.onErrorContainer, p.errorContainer)
        assertBody("$name onSuccess/success", p.onSuccess, p.success)
        assertBody("$name onWarning/warning", p.onWarning, p.warning)
    }

    private fun assertPaletteUi(name: String, p: PatagoniaPalette) {
        assertUi("$name outline/background", p.outline, p.background)
        assertUi("$name primary/background (UI accent)", p.primary, p.background)
    }

    @Test
    fun `light palette clears AA contrast`() {
        assertPaletteBody("light", PatagoniaLightPalette)
        assertPaletteUi("light", PatagoniaLightPalette)
    }

    @Test
    fun `dark palette clears AA contrast`() {
        assertPaletteBody("dark", PatagoniaDarkPalette)
        assertPaletteUi("dark", PatagoniaDarkPalette)
    }

    @Test
    fun `light palette is light and dark palette is dark`() {
        assertTrue(
            "light background must be lighter than dark background",
            luminance(PatagoniaLightPalette.background) > luminance(PatagoniaDarkPalette.background)
        )
        assertTrue(
            "light onSurface must be darker than dark onSurface (ink vs bright text)",
            luminance(PatagoniaLightPalette.onSurface) < luminance(PatagoniaDarkPalette.onSurface)
        )
    }
}
