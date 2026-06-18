package com.patagonia.app.domain.model

/**
 * Species category enum used to determine map pin icon (D-19).
 * Each category maps to a distinct icon on the map:
 * - MAMMAL → paw print
 * - BIRD → bird silhouette
 * - PLANT → leaf
 * - FUNGI → mushroom
 * - UNKNOWN → generic pin
 */
enum class SpeciesCategory {
    MAMMAL,
    BIRD,
    PLANT,
    FUNGI,
    UNKNOWN;

    companion object {
        /**
         * Infer category from species name heuristics.
         * In a production app, this would be backed by a species database lookup.
         */
        fun fromSpeciesName(name: String): SpeciesCategory {
            val lower = name.lowercase()
            return when {
                // Common Chilean mammals
                lower.contains("puma") || lower.contains("huemul") ||
                lower.contains("fox") || lower.contains("zorro") ||
                lower.contains("guanaco") || lower.contains("vicuña") ||
                lower.contains("chinchilla") || lower.contains("pudú") -> MAMMAL

                // Common Chilean birds
                lower.contains("condor") || lower.contains("cóndor") ||
                lower.contains("penguin") || lower.contains("pingüino") ||
                lower.contains("flamingo") || lower.contains("hawk") ||
                lower.contains("eagle") || lower.contains("hummingbird") ||
                lower.contains("woodpecker") || lower.contains("owl") -> BIRD

                // Plants
                lower.contains("araucaria") || lower.contains("monkey puzzle") ||
                lower.contains("copihue") || lower.contains("fern") ||
                lower.contains("tree") || lower.contains("flower") ||
                lower.contains("bush") || lower.contains("moss") -> PLANT

                // Fungi
                lower.contains("mushroom") || lower.contains("fungi") ||
                lower.contains("hongo") || lower.contains("lichen") -> FUNGI

                else -> UNKNOWN
            }
        }
    }
}
