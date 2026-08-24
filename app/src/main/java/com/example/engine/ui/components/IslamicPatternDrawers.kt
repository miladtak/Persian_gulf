package com.example.engine.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.GulfCyan
import com.example.ui.theme.GulfGold
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom Canvas drawing for Islamic Star & Geometric Arabesque Background Patterns
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.08f,
    lineColor: Color = GulfGold
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val step = 64f
        
        var x = 0f
        while (x < width) {
            var y = 0f
            while (y < height) {
                drawIslamicOctagram(
                    center = Offset(x, y),
                    radius = 24f,
                    color = lineColor.copy(alpha = alpha),
                    strokeWidth = 1f
                )
                y += step
            }
            x += step
        }
    }
}

/**
 * Draws Islamic Corner Arabesque flourish
 */
@Composable
fun IslamicCornerFlourish(
    modifier: Modifier = Modifier,
    color: Color = GulfGold.copy(alpha = 0.25f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Draw corner arcs and geometric interlacing
        val path = Path().apply {
            moveTo(0f, 0f)
            cubicTo(w * 0.4f, 0f, w, h * 0.4f, w, h)
            lineTo(w * 0.8f, h)
            cubicTo(w * 0.8f, h * 0.5f, w * 0.5f, h * 0.2f, 0f, h * 0.2f)
            close()
        }
        drawPath(path, color = color)
        
        // 8-pointed star in corner
        drawIslamicOctagram(
            center = Offset(w * 0.3f, h * 0.3f),
            radius = 12f,
            color = GulfCyan.copy(alpha = 0.35f),
            strokeWidth = 1.5f
        )
    }
}

fun DrawScope.drawIslamicOctagram(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float
) {
    // 8-pointed star made of two interleaved squares rotated 45 degrees
    val path = Path()
    val points = 8
    val innerRadius = radius * 0.54f
    
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = (i * PI / points).toFloat() - (PI / 2).toFloat()
        val px = center.x + r * cos(angle)
        val py = center.y + r * sin(angle)
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}
