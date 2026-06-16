package com.patagonia.app.domain.model

data class Recognition(
    val title: String,
    val confidence: Float,
    val scientificName: String? = null
)
