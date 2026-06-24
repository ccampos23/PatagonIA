package com.patagonia.app.presentation

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.relativeTo

/**
 * Keeps prototype emoji/text glyphs out of production Compose UI.
 *
 * PataGOnIA's product register is an outdoor field tool, so iconography should be explicit,
 * theme-tinted Material Icons rather than device-rendered emoji or text symbols.
 */
class TextIconographyTest {

    private val forbiddenGlyphs = listOf("📷", "🗺", "🌿", "🖼", "ℹ")

    @Test
    fun `presentation UI does not use emoji or text glyphs as icons`() {
        val presentationDir = resolvePresentationDir()
        val violations = Files.walk(presentationDir).use { paths ->
            paths
                .filter { it.extension == "kt" }
                .flatMap { path ->
                    val relativePath = path.relativeTo(presentationDir).toString()
                    Files.readAllLines(path).mapIndexedNotNull { index, line ->
                        val glyph = forbiddenGlyphs.firstOrNull { it in line }
                        if (glyph == null) null else "$relativePath:${index + 1} contains $glyph"
                    }.stream()
                }
                .toList()
        }

        assertTrue(
            "Replace text glyphs with Material Icon composables:\n${violations.joinToString("\n")}",
            violations.isEmpty(),
        )
    }

    private fun resolvePresentationDir(): Path {
        val workingDir = Paths.get(System.getProperty("user.dir"))
        val candidates = listOf(
            workingDir.resolve("src/main/java/com/patagonia/app/presentation"),
            workingDir.resolve("app/src/main/java/com/patagonia/app/presentation"),
        )

        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Could not locate presentation source directory from $workingDir")
    }
}
