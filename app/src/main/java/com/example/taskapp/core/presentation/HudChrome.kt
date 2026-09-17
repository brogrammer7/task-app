package com.example.taskapp.core.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

/**
 * Fakes emitted light by stacking progressively tighter, brighter rectangles behind
 * an element. Deliberately not [androidx.compose.ui.draw.blur], which needs API 31+
 * and degrades to nothing on older devices — this falls off identically everywhere.
 */
fun Modifier.bloom(
    color: Color,
    spread: Dp = 10.dp,
    layers: Int = 3
): Modifier = drawBehind {
    val spreadPx = spread.toPx()
    for (i in layers downTo 1) {
        val t = i / layers.toFloat() // 1.0 at the outermost, faintest pass
        val inset = spreadPx * t
        drawRect(
            color = color.copy(alpha = 0.10f / i),
            topLeft = Offset(-inset, -inset),
            size = Size(size.width + inset * 2, size.height + inset * 2)
        )
    }
}

/**
 * A hairline that carries the accent at full strength on the left and fades out —
 * a lit seam rather than a border. Reads as the edge of an illuminated panel.
 */
@Composable
fun HudRule(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawRect(
            brush = Brush.horizontalGradient(
                0f to color.copy(alpha = 0.9f),
                0.4f to color.copy(alpha = 0.25f),
                1f to Color.Transparent
            )
        )
    }
}

/**
 * The screen header. Not a [androidx.compose.material3.TopAppBar] — that centers a
 * single string and has nowhere to put a live readout, which is the whole point here.
 *
 * @param readout optional telemetry line beneath the title, e.g. active/total counts.
 * @param leading optional control on the left, e.g. a back affordance.
 */
@Composable
fun HudHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    readout: (@Composable RowScope.() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // An IconButton carries its own 12dp of internal padding, so the
                // gutter shrinks when one is present to keep the optical margin equal.
                .padding(start = if (leading != null) 8.dp else 20.dp, end = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()
            }
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (readout != null) {
            Row(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = readout
            )
        }

        HudRule(modifier = Modifier.padding(top = 16.dp))
    }
}
