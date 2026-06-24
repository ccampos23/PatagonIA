package com.patagonia.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/*
 * Patagonia shape scale.
 *
 * One corner-radius ramp for the whole app, read through MaterialTheme.shapes so components
 * and explicit .clip() share a single source. Outdoor-tool register: crisp, modest radii —
 * no oversized pills. The 12dp medium is the workhorse for buttons, fields and inner cards;
 * 16dp large for outer cards, images and tooltips; 4dp for thin progress bars.
 */
val PatagoniaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
