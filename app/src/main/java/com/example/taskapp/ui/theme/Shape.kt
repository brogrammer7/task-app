package com.example.taskapp.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Nothing is rounded. Panels meet at right angles the way hardware does, and the
 * only break in that rule is a cut corner — a bevel, not a curve.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

/** The one beveled element on screen: the add-task control. */
val BevelShape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
