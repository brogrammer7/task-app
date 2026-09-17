package com.example.taskapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Neon-futurist palette, built like an instrument panel lit at night.
 *
 * The ground tones are all blue-black rather than pure black so the accents read
 * as emitted light rather than as flat swatches sitting on a void.
 */

// Ground
val VoidNavy = Color(0xFF070B14)      // background — the unlit void
val PanelNavy = Color(0xFF0E1626)     // surface — a raised instrument panel
val PanelNavyLift = Color(0xFF16213A) // surfaceVariant — inset / pressed panel
val EdgeNavy = Color(0xFF23304D)      // outline — hairline seams between panels

// Emitted light
val NeonCyan = Color(0xFF3DF5FF)      // primary — live, powered, pending
val SignalAmber = Color(0xFFFFB23D)   // secondary — attention, in flight
val PlasmaMagenta = Color(0xFFFF3D8B) // error — destructive
val MagentaWell = Color(0xFF3D0A1E)   // errorContainer — the recess behind a delete

// Text
val FrostWhite = Color(0xFFE6F4FF)    // onSurface — blue-tinted, never pure white
val DimSlate = Color(0xFF7A92AA)      // onSurfaceVariant — retired / completed
