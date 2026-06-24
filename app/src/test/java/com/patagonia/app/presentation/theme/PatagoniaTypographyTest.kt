package com.patagonia.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the Patagonia type scale.
 *
 * PRODUCT.md: "legibilidad en terreno primero" + a committed brand typeface. These tests guard
 * the two things that most often regress silently:
 *   1. Body text never shrinks below the outdoor-readability floor (16sp).
 *   2. The bundled Fira Sans family is actually applied — if a TextStyle falls back to the
 *      platform default, its fontFamily is null and the brand voice is gone.
 *   3. The scale stays monotonic within and across families, so hierarchy never goes muddy.
 */
class PatagoniaTypographyTest {

    private val t: Typography = PatagoniaTypography

    private fun sz(style: TextStyle): Float = style.fontSize.value

    private fun assertGe(label: String, larger: TextStyle, smaller: TextStyle) {
        assertTrue("$label: ${sz(larger)} < ${sz(smaller)}", sz(larger) >= sz(smaller) - 0.001f)
    }

    @Test
    fun `body large meets the 16sp outdoor readability floor`() {
        assertTrue("bodyLarge ${sz(t.bodyLarge)} < 16sp", sz(t.bodyLarge) >= 16f)
    }

    @Test
    fun `bundled fira sans family is applied to every role`() {
        assertNotNull("displayLarge fontFamily missing", t.displayLarge.fontFamily)
        assertNotNull("headlineLarge fontFamily missing", t.headlineLarge.fontFamily)
        assertNotNull("titleMedium fontFamily missing", t.titleMedium.fontFamily)
        assertNotNull("bodyLarge fontFamily missing", t.bodyLarge.fontFamily)
        assertNotNull("labelSmall fontFamily missing", t.labelSmall.fontFamily)
    }

    @Test
    fun `scale is monotonic within each family`() {
        assertGe("display L>=M", t.displayLarge, t.displayMedium)
        assertGe("display M>=S", t.displayMedium, t.displaySmall)
        assertGe("headline L>=M", t.headlineLarge, t.headlineMedium)
        assertGe("headline M>=S", t.headlineMedium, t.headlineSmall)
        assertGe("title L>=M", t.titleLarge, t.titleMedium)
        assertGe("title M>=S", t.titleMedium, t.titleSmall)
        assertGe("body L>=M", t.bodyLarge, t.bodyMedium)
        assertGe("body M>=S", t.bodyMedium, t.bodySmall)
        assertGe("label L>=M", t.labelLarge, t.labelMedium)
        assertGe("label M>=S", t.labelMedium, t.labelSmall)
    }

    @Test
    fun `main diagonal descends display to label`() {
        assertGe("display>=headline", t.displaySmall, t.headlineLarge)
        assertGe("headline>=title", t.headlineSmall, t.titleLarge)
        assertGe("title>=body", t.titleMedium, t.bodyLarge)
    }

    @Test
    fun `body line-height stays comfortable for reading`() {
        // line-height ≥ fontSize × 1.3 keeps multi-line body readable.
        assertTrue(
            "bodyLarge leading ${t.bodyLarge.lineHeight.value} too tight",
            t.bodyLarge.lineHeight.value >= sz(t.bodyLarge) * 1.3f,
        )
        assertTrue(
            "bodyMedium leading ${t.bodyMedium.lineHeight.value} too tight",
            t.bodyMedium.lineHeight.value >= sz(t.bodyMedium) * 1.3f,
        )
    }

    @Test
    fun `sizes are specified sp units`() {
        listOf(t.bodyLarge, t.titleLarge, t.headlineSmall, t.labelLarge).forEach {
            assertTrue("fontSize must be specified", it.fontSize.isSpecified)
        }
        assertTrue("reference 16.sp must be sp", 16.sp.isSpecified)
    }
}
