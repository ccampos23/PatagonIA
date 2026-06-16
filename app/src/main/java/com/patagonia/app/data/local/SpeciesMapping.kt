package com.patagonia.app.data.local

object SpeciesMapping {
    fun getCommonName(scientificName: String): String {
        return when (scientificName.trim().lowercase()) {
            "puma concolor" -> "Puma"
            "vultur gryphus" -> "Cóndor Andino"
            "lama guanicoe" -> "Guanaco"
            "lycalopex griseus" -> "Zorro Chilla"
            "lycalopex culpaeus" -> "Zorro Culpeo"
            "hippocamelus bisulcus" -> "Huemul"
            "pudu puda" -> "Pudú"
            else -> scientificName
        }
    }

    fun getScientificName(commonName: String): String {
        return when (commonName.trim().lowercase()) {
            "puma" -> "Puma concolor"
            "cóndor andino", "condor andino", "cóndor", "condor" -> "Vultur gryphus"
            "guanaco" -> "Lama guanicoe"
            "zorro chilla" -> "Lycalopex griseus"
            "zorro culpeo" -> "Lycalopex culpaeus"
            "huemul" -> "Hippocamelus bisulcus"
            "pudú", "pudu" -> "Pudu puda"
            else -> "Species indet."
        }
    }
}
