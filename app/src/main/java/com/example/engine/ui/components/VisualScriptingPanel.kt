package com.example.engine.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.model.*
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun VisualScriptingPanel(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFF1E242E)) // Dark slate background matching image_0.png
            .border(1.dp, GulfNavyBorder)
    ) {
        // Grid dots background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = 28f
            var x = 0f
            while (x < w) {
                var y = 0f
                while (y < h) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 1.2f,
                        center = Offset(x, y)
                    )
                    y += step
                }
                x += step
            }
        }

        // 1. Interactive Bezier Connecting Wires
        Canvas(modifier = Modifier.fillMaxSize()) {
            uiState.scriptConnections.forEach { conn ->
                val fromNode = uiState.scriptNodes.find { it.id == conn.fromNodeId }
                val toNode = uiState.scriptNodes.find { it.id == conn.toNodeId }

                if (fromNode != null && toNode != null) {
                    val startX = (fromNode.x + 190f) * 1.5f // Output socket position
                    val startY = (fromNode.y + 22f) * 1.5f
                    val endX = toNode.x * 1.5f // Input socket position
                    val endY = (toNode.y + 22f) * 1.5f

                    val controlX1 = startX + (endX - startX) * 0.5f
                    val controlY1 = startY
                    val controlX2 = startX + (endX - startX) * 0.5f
                    val controlY2 = endY

                    val path = Path().apply {
                        moveTo(startX, startY)
                        cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY)
                    }

                    // Wire shadow / glow
                    drawPath(
                        path = path,
                        color = if (uiState.isScriptExecuting) GulfCyan else Color(0xFF4A9BE8).copy(alpha = 0.8f),
                        style = Stroke(width = if (uiState.isScriptExecuting) 4.5f else 3f, cap = StrokeCap.Round)
                    )
                    // Wire inner highlight
                    drawPath(
                        path = path,
                        color = if (uiState.isScriptExecuting) Color.White else Color(0xFFB3E5FC),
                        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                    )
                }
            }
        }

        // 2. Visual Script Nodes (Blue blocks matching image_0.png)
        Box(modifier = Modifier.fillMaxSize()) {
            uiState.scriptNodes.forEach { node ->
                VisualBlockNodeCard(
                    node = node,
                    lang = lang,
                    isExecuting = uiState.isScriptExecuting,
                    onDrag = { dx, dy ->
                        viewModel.moveNode(node.id, dx / 1.5f, dy / 1.5f)
                    }
                )
            }
        }

        // 3. Top Action Bar for Visual Scripting
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { viewModel.setShowAddNodeMenu(true) },
                    modifier = Modifier.height(28.dp).testTag("add_node_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfCyan, GulfNavyBorder))),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = GulfCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(EngineStrings.addNode(lang), fontSize = 11.sp, color = Color.White)
                }

                Button(
                    onClick = { viewModel.runVisualScript() },
                    modifier = Modifier.height(28.dp).testTag("run_script_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.isScriptExecuting) GulfAmber else Color(0xFF1E88E5)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isScriptExecuting) Icons.Default.Bolt else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(EngineStrings.runGraph(lang), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Status chip
            Surface(
                color = GulfNavyDeep.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.5f))))
            ) {
                Text(
                    text = if (uiState.isScriptExecuting) "⚡ RUNNING" else "SCRIPT GRAPH READY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isScriptExecuting) GulfCyan else TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Individual Node Block styled after the blue rectangular blocks in image_0.png
 */
@Composable
fun VisualBlockNodeCard(
    node: ScriptBlockNode,
    lang: EngineLanguage,
    isExecuting: Boolean,
    onDrag: (Float, Float) -> Unit
) {
    val isRtl = lang == EngineLanguage.PERSIAN
    val titleText = if (lang == EngineLanguage.ENGLISH) node.title else node.persianTitle
    val subText = if (lang == EngineLanguage.ENGLISH) node.subtitle else (node.persianSubtitle ?: node.subtitle)

    Box(
        modifier = Modifier
            .offset(x = (node.x * 1.5f).dp, y = (node.y * 1.5f).dp)
            .widthIn(min = 150.dp, max = 220.dp)
            .shadow(if (isExecuting) 8.dp else 4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF388AE6),
                        Color(0xFF2C74C9),
                        Color(0xFF1E60B5)
                    )
                )
            )
            .border(
                1.5.dp,
                if (isExecuting) GulfCyan else Color(0xFF70B6FA),
                RoundedCornerShape(8.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            // Node Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )
                // Flow connector socket
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF81D4FA))
                        .border(1.dp, Color.White, CircleShape)
                )
            }

            // Subtitle or Persian property if exists (e.g. "Son", "تعداد آیتم", "fx", "x")
            if (subText != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFE1F5FE),
                            fontSize = 10.sp
                        )
                    )
                    // Secondary socket
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GulfGold)
                    )
                }
            }
        }
    }
}
