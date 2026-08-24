package com.example.engine.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.audio.SfxSoundType
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.model.*
import com.example.engine.particles.ParticlePresetType
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun SceneViewportCanvas(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN

    // Animation ticker for ocean waves and weather particles
    val infiniteTransition = rememberInfiniteTransition(label = "ViewportAnimation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveOffset"
    )
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainOffset"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(GulfNavyDeep)
            .border(1.dp, GulfNavyBorder)
    ) {
        // 1. Custom 2D Viewport Canvas
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = maxWidth
            val canvasHeight = maxHeight

            // Interactive Canvas for Objects, Sky, Ocean, Terrain, Gizmos
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(uiState.activeTool) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val selId = uiState.selectedObjectId
                            if (selId != null && uiState.activeTool == TransformMode.TRANSLATE) {
                                viewModel.moveSceneObject(selId, dragAmount.x, dragAmount.y)
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw Sky & Clouds
                drawSkyAndAtmosphere(w, h, uiState.currentWeather)

                // Draw Persian Gulf Ocean Waves
                drawPersianGulfOcean(w, h, waveOffset)

                // Draw Island Terrain & Cliff
                drawIslandTerrain(w, h)

                // Draw Palm Tree foliage
                drawPalmTree(center = Offset(w * 0.22f, h * 0.52f))

                // Draw Grid if enabled
                if (uiState.showGrid) {
                    drawEditorGrid(w, h)
                }

                // Draw Dynamic Weather Particles
                drawWeatherEffects(w, h, uiState.currentWeather, rainOffset)

                // Draw Real-time Active Particles from Particle System Engine
                uiState.activeParticles.forEach { particle ->
                    drawCircle(
                        color = particle.color,
                        radius = particle.size,
                        center = Offset(particle.x, particle.y)
                    )
                }

                // Draw Collider wireframes if enabled
                if (uiState.showColliders) {
                    uiState.sceneObjects.forEach { obj ->
                        val col = obj.collider
                        drawRect(
                            color = if (col.isTrigger) Color(0xFFFF9100).copy(alpha = 0.5f) else Color(0xFF00E676).copy(alpha = 0.4f),
                            topLeft = Offset(obj.x + col.offsetX, obj.y + col.offsetY),
                            size = Size(obj.width, obj.height),
                            style = Stroke(width = 1.5f)
                        )
                    }
                }
            }

            // 2. Overlay Sprites with Selection Gizmo Boxes
            // Sailor Hero (Player)
            val playerHero = uiState.sceneObjects.find { it.type == SceneObjectType.PLAYER }
            if (playerHero != null) {
                val renderX = if (uiState.isPlayMode) uiState.playerX else playerHero.x
                val renderY = if (uiState.isPlayMode) uiState.playerY else playerHero.y
                Box(
                    modifier = Modifier
                        .offset(
                            x = (renderX * uiState.zoomLevel).dp,
                            y = (renderY * uiState.zoomLevel).dp
                        )
                        .size(playerHero.width.dp, playerHero.height.dp)
                        .clickable { viewModel.selectSceneObject(playerHero.id) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sailor_character_1787576690972),
                        contentDescription = "Sailor Hero",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                    )

                    if (playerHero.isSelected && !uiState.isPlayMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.5.dp, Color(0xFF64B5F6), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-8).dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Box(modifier = Modifier.align(Alignment.TopStart).size(6.dp).background(Color.White))
                            Box(modifier = Modifier.align(Alignment.TopEnd).size(6.dp).background(Color.White))
                            Box(modifier = Modifier.align(Alignment.BottomStart).size(6.dp).background(Color.White))
                            Box(modifier = Modifier.align(Alignment.BottomEnd).size(6.dp).background(Color.White))
                        }
                    }
                }
            }

            // Persian Dhow Boat Sprite
            val shipBoat = uiState.sceneObjects.find { it.type == SceneObjectType.SHIP_DHOW }
            if (shipBoat != null) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (shipBoat.x * uiState.zoomLevel).dp,
                            y = (shipBoat.y * uiState.zoomLevel).dp
                        )
                        .size(shipBoat.width.dp, shipBoat.height.dp)
                        .clickable { viewModel.selectSceneObject(shipBoat.id) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dhow_ship_sprite_1787576667436),
                        contentDescription = "Persian Dhow Boat",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                    )

                    if (shipBoat.isSelected && !uiState.isPlayMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(1.2.dp, Color(0xFF4FC3F7), RoundedCornerShape(2.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cx = size.width * 0.5f
                                val cy = size.height * 0.5f
                                drawLine(Color(0xFFE53935), Offset(cx, cy), Offset(cx + 40f, cy), strokeWidth = 3f)
                                drawLine(Color(0xFF43A047), Offset(cx, cy), Offset(cx, cy - 40f), strokeWidth = 3f)
                                drawLine(Color(0xFF1E88E5), Offset(cx, cy), Offset(cx - 25f, cy + 25f), strokeWidth = 3f)
                                drawRect(Color.White, topLeft = Offset(cx - 4f, cy - 4f), size = Size(8f, 8f))
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-8).dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    }
                }
            }

            // Gold Treasure Chest with Particle Burst Trigger
            val treasureChest = uiState.sceneObjects.find { it.type == SceneObjectType.TREASURE_CHEST }
            if (treasureChest != null) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (treasureChest.x * uiState.zoomLevel).dp,
                            y = (treasureChest.y * uiState.zoomLevel).dp
                        )
                        .size(treasureChest.width.dp, treasureChest.height.dp)
                        .clickable {
                            viewModel.selectSceneObject(treasureChest.id)
                            viewModel.playSfx(SfxSoundType.COIN_COLLECT)
                            viewModel.burstParticleEffect(ParticlePresetType.PERSIAN_GOLD_DUST, treasureChest.x, treasureChest.y)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF6D4C41))
                            .border(1.5.dp, GulfGold, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = "Gold Chest", tint = GulfGold, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Enemy Guard with Health Bar & Power Bar
            val enemyGuard = uiState.sceneObjects.find { it.type == SceneObjectType.ENEMY_GUARD }
            if (enemyGuard != null) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (enemyGuard.x * uiState.zoomLevel).dp,
                            y = (enemyGuard.y * uiState.zoomLevel).dp
                        )
                        .size(enemyGuard.width.dp, enemyGuard.height.dp)
                        .clickable { viewModel.selectSceneObject(enemyGuard.id) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val hpPercent = (enemyGuard.aiProfile?.currentHealth ?: 100f) / 100f
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(hpPercent)
                                    .background(Color(0xFFFF3D00))
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        val pwrPercent = (enemyGuard.aiProfile?.currentPower ?: 70f) / 100f
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(pwrPercent)
                                    .background(GulfCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4A148C).copy(alpha = 0.8f))
                                .border(1.dp, Color(0xFFBA68C8), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Enemy Guard",
                                tint = Color(0xFFE1BEE7),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = if (enemyGuard.aiProfile?.isAggro == true) "⚔️ AGGRO" else "🛡️ PATROL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                color = if (enemyGuard.aiProfile?.isAggro == true) Color(0xFFFF5252) else GulfGold
                            )
                        )
                    }
                }
            }
        }

        // 3. Top-Right Viewport Actions (Settings, Eye, Weather, Save/Load, Fullscreen)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GulfNavyDark.copy(alpha = 0.85f))
                .border(1.dp, GulfNavyBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Projects / Save / Load Dialog Button
            IconButton(
                onClick = { viewModel.setShowProjectSaveLoad(true) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = "Project Save/Load",
                    tint = GulfGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Settings menu
            IconButton(
                onClick = { viewModel.setShowSettings(true) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Viewport Settings",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Eye / View Colliders toggle
            IconButton(
                onClick = { viewModel.toggleGrid() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (uiState.showGrid) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Grid/Colliders",
                    tint = if (uiState.showGrid) GulfCyan else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Weather switcher dropdown button
            var showWeatherDropdown by remember { mutableStateOf(false) }
            Box {
                IconButton(
                    onClick = { showWeatherDropdown = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = when (uiState.currentWeather) {
                            WeatherType.SUNNY_GULF -> Icons.Default.WbSunny
                            WeatherType.TROPICAL_RAIN -> Icons.Default.WaterDrop
                            WeatherType.GULF_MIST_SNOW -> Icons.Default.AcUnit
                            WeatherType.THUNDER_STORM -> Icons.Default.Thunderstorm
                        },
                        contentDescription = "Weather",
                        tint = GulfGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                DropdownMenu(
                    expanded = showWeatherDropdown,
                    onDismissRequest = { showWeatherDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(EngineStrings.sunny(lang)) },
                        onClick = { viewModel.setWeather(WeatherType.SUNNY_GULF); showWeatherDropdown = false },
                        leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null, tint = GulfGold) }
                    )
                    DropdownMenuItem(
                        text = { Text(EngineStrings.rain(lang)) },
                        onClick = { viewModel.setWeather(WeatherType.TROPICAL_RAIN); showWeatherDropdown = false },
                        leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = GulfCyan) }
                    )
                    DropdownMenuItem(
                        text = { Text(EngineStrings.mistSnow(lang)) },
                        onClick = { viewModel.setWeather(WeatherType.GULF_MIST_SNOW); showWeatherDropdown = false },
                        leadingIcon = { Icon(Icons.Default.AcUnit, contentDescription = null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text(EngineStrings.storm(lang)) },
                        onClick = { viewModel.setWeather(WeatherType.THUNDER_STORM); showWeatherDropdown = false },
                        leadingIcon = { Icon(Icons.Default.Thunderstorm, contentDescription = null, tint = Color(0xFFFFD54F)) }
                    )
                }
            }

            // Expand / Fullscreen
            IconButton(
                onClick = { viewModel.togglePlayMode() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Expand to Game Mode",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 4. Right Strip of Viewport (Ship, Layers, Delete, Transform) - matches image_0.png
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 56.dp)
                .padding(end = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GulfNavyDark.copy(alpha = 0.85f))
                .border(1.dp, GulfNavyBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Particle burst test button
            IconButton(
                onClick = { viewModel.burstParticleEffect(ParticlePresetType.PERSIAN_GOLD_DUST) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Burst Particles",
                    tint = GulfCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Ship quick add
            IconButton(
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.SHIP_DHOW) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBoat,
                    contentDescription = "Add Ship",
                    tint = GulfGold,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Layers
            IconButton(
                onClick = { viewModel.toggleLayers() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Layers",
                    tint = if (uiState.showLayers) GulfCyan else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Delete object
            IconButton(
                onClick = { viewModel.deleteSelectedObject() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Transform arrows
            IconButton(
                onClick = { viewModel.setTransformTool(TransformMode.TRANSLATE) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenWith,
                    contentDescription = "Transform",
                    tint = if (uiState.activeTool == TransformMode.TRANSLATE) GulfCyan else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 5. On-Screen Touch Controls HUD Preview (Matches image_0.png bottom right overlay)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 44.dp)
                .width(260.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF041022).copy(alpha = 0.72f))
                .border(1.5.dp, GulfCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // D-Pad / Joystick (Left side of HUD)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF162D4A).copy(alpha = 0.8f))
                        .border(1.dp, GulfNavyBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Up",
                        tint = TextSecondary,
                        modifier = Modifier.align(Alignment.TopCenter).size(22.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Down",
                        tint = TextSecondary,
                        modifier = Modifier.align(Alignment.BottomCenter).size(22.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowLeft,
                        contentDescription = "Left",
                        tint = TextSecondary,
                        modifier = Modifier.align(Alignment.CenterStart).size(22.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = "Right",
                        tint = TextSecondary,
                        modifier = Modifier.align(Alignment.CenterEnd).size(22.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF264773))
                            .border(1.dp, GulfCyan.copy(alpha = 0.6f), CircleShape)
                    )
                }

                // Action Buttons Grid (Right side of HUD)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HudCircleButton(label = "Jump", sizeDp = 28) { viewModel.onPlayerJump() }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HudCircleButton(label = "Dodge", sizeDp = 26) { viewModel.onPlayerDodge() }
                        HudCircleButton(label = "Attack", sizeDp = 30, color = Color(0xFFEF5350)) { viewModel.onPlayerAttack() }
                    }

                    HudCircleButton(label = "Bag", sizeDp = 26, color = GulfGold) { viewModel.setShowInventory(true) }
                }
            }
        }

        // 6. Bottom Viewport Bar (Zoom in/out, Zoom Slider, Mute, Fullscreen)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(34.dp)
                .background(GulfNavyDeep.copy(alpha = 0.9f))
                .border(0.8.dp, GulfNavyBorder)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp).clickable { viewModel.setZoom(uiState.zoomLevel + 0.1f) }
                )
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp).clickable { viewModel.setZoom(uiState.zoomLevel - 0.1f) }
                )
                Slider(
                    value = uiState.zoomLevel,
                    onValueChange = { viewModel.setZoom(it) },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.width(100.dp).height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = GulfCyan,
                        activeTrackColor = GulfCyan,
                        inactiveTrackColor = GulfNavyBorder
                    )
                )
                Text(
                    text = "${(uiState.zoomLevel * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute Sound",
                        tint = if (uiState.isMuted) Color(0xFFEF5350) else GulfCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePlayMode() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen Game",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HudCircleButton(
    label: String,
    sizeDp: Int,
    color: Color = Color(0xFF1E88E5),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.35f))
            .border(1.dp, color.copy(alpha = 0.8f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

fun DrawScope.drawSkyAndAtmosphere(w: Float, h: Float, weather: WeatherType) {
    val skyColors = when (weather) {
        WeatherType.SUNNY_GULF -> listOf(Color(0xFF81D4FA), Color(0xFFB3E5FC), Color(0xFFE1F5FE))
        WeatherType.TROPICAL_RAIN -> listOf(Color(0xFF455A64), Color(0xFF607D8B), Color(0xFF90A4AE))
        WeatherType.GULF_MIST_SNOW -> listOf(Color(0xFF78909C), Color(0xFFB0BEC5), Color(0xFFECEFF1))
        WeatherType.THUNDER_STORM -> listOf(Color(0xFF263238), Color(0xFF37474F), Color(0xFF455A64))
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = skyColors,
            startY = 0f,
            endY = h * 0.7f
        ),
        size = Size(w, h * 0.7f)
    )

    if (weather == WeatherType.SUNNY_GULF) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F).copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.85f, h * 0.15f),
                radius = 65f
            ),
            center = Offset(w * 0.85f, h * 0.15f),
            radius = 65f
        )
    }

    drawCloud(Offset(w * 0.2f, h * 0.12f), 80f)
    drawCloud(Offset(w * 0.65f, h * 0.08f), 110f)
    drawCloud(Offset(w * 0.85f, h * 0.2f), 70f)
}

fun DrawScope.drawCloud(center: Offset, width: Float) {
    val cloudColor = Color.White.copy(alpha = 0.85f)
    val r = width * 0.22f
    drawCircle(cloudColor, radius = r, center = Offset(center.x - r * 0.9f, center.y))
    drawCircle(cloudColor, radius = r * 1.3f, center = Offset(center.x, center.y - r * 0.4f))
    drawCircle(cloudColor, radius = r, center = Offset(center.x + r * 0.9f, center.y))
    drawRoundRect(
        color = cloudColor,
        topLeft = Offset(center.x - r * 1.2f, center.y - r * 0.2f),
        size = Size(r * 2.4f, r * 1.1f),
        cornerRadius = CornerRadius(r, r)
    )
}

fun DrawScope.drawPersianGulfOcean(w: Float, h: Float, wavePhase: Float) {
    val oceanTop = h * 0.62f
    val oceanBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00ACC1),
            Color(0xFF00838F),
            Color(0xFF006064),
            Color(0xFF004D40)
        ),
        startY = oceanTop,
        endY = h
    )

    val wavePath = Path().apply {
        moveTo(0f, oceanTop)
        var x = 0f
        while (x <= w) {
            val waveHeight = 8f * sin((x / 60f) + wavePhase).toFloat()
            lineTo(x, oceanTop + waveHeight)
            x += 20f
        }
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(wavePath, brush = oceanBrush)

    val foamPath = Path().apply {
        moveTo(0f, oceanTop + 3f)
        var x = 0f
        while (x <= w) {
            val waveHeight = 8f * sin((x / 60f) + wavePhase).toFloat()
            lineTo(x, oceanTop + waveHeight + 2f)
            x += 20f
        }
    }
    drawPath(foamPath, color = Color.White.copy(alpha = 0.55f), style = Stroke(width = 2.5f))
}

fun DrawScope.drawIslandTerrain(w: Float, h: Float) {
    val cliffLeft = 0f
    val cliffRight = w * 0.42f
    val groundTop = h * 0.58f

    val cliffPath = Path().apply {
        moveTo(cliffLeft, groundTop)
        lineTo(cliffRight, groundTop)
        lineTo(cliffRight + 15f, groundTop + 20f)
        lineTo(cliffRight + 10f, groundTop + 45f)
        lineTo(cliffRight + 20f, groundTop + 65f)
        lineTo(cliffRight - 10f, groundTop + 90f)
        lineTo(cliffLeft, groundTop + 90f)
        close()
    }
    drawPath(
        cliffPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF8D6E63), Color(0xFF6D4C41), Color(0xFF4E342E)),
            startY = groundTop,
            endY = groundTop + 90f
        )
    )

    val grassPath = Path().apply {
        moveTo(cliffLeft, groundTop)
        lineTo(cliffRight + 12f, groundTop)
        lineTo(cliffRight + 12f, groundTop + 14f)
        var x = cliffRight + 12f
        while (x >= cliffLeft) {
            lineTo(x, groundTop + 14f + (if (x.toInt() % 16 == 0) 4f else 0f))
            x -= 12f
        }
        lineTo(cliffLeft, groundTop)
        close()
    }
    drawPath(
        grassPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
            startY = groundTop,
            endY = groundTop + 16f
        )
    )
}

fun DrawScope.drawPalmTree(center: Offset) {
    val trunkPath = Path().apply {
        moveTo(center.x - 6f, center.y + 70f)
        cubicTo(center.x - 20f, center.y + 20f, center.x - 10f, center.y - 30f, center.x + 10f, center.y - 70f)
        lineTo(center.x + 20f, center.y - 70f)
        cubicTo(center.x, center.y - 30f, center.x - 8f, center.y + 20f, center.x + 8f, center.y + 70f)
        close()
    }
    drawPath(trunkPath, color = Color(0xFF5D4037))

    val top = Offset(center.x + 15f, center.y - 70f)
    drawPalmFrond(top, Offset(top.x - 60f, top.y - 20f))
    drawPalmFrond(top, Offset(top.x - 70f, top.y + 20f))
    drawPalmFrond(top, Offset(top.x + 60f, top.y - 25f))
    drawPalmFrond(top, Offset(top.x + 75f, top.y + 15f))
    drawPalmFrond(top, Offset(top.x, top.y - 50f))

    drawCircle(Color(0xFF3E2723), radius = 6f, center = Offset(top.x - 4f, top.y + 4f))
    drawCircle(Color(0xFF3E2723), radius = 6f, center = Offset(top.x + 6f, top.y + 6f))
}

fun DrawScope.drawPalmFrond(start: Offset, end: Offset) {
    val path = Path().apply {
        moveTo(start.x, start.y)
        val cx = (start.x + end.x) / 2f
        val cy = (start.y + end.y) / 2f - 25f
        cubicTo(cx, cy, end.x, end.y - 10f, end.x, end.y)
        cubicTo(end.x, end.y + 10f, cx, cy + 15f, start.x, start.y)
        close()
    }
    drawPath(path, color = Color(0xFF43A047))
}

fun DrawScope.drawEditorGrid(w: Float, h: Float) {
    val step = 40f
    var x = 0f
    while (x < w) {
        drawLine(Color.White.copy(alpha = 0.06f), Offset(x, 0f), Offset(x, h), strokeWidth = 0.8f)
        x += step
    }
    var y = 0f
    while (y < h) {
        drawLine(Color.White.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y), strokeWidth = 0.8f)
        y += step
    }
}

fun DrawScope.drawWeatherEffects(w: Float, h: Float, weather: WeatherType, offset: Float) {
    when (weather) {
        WeatherType.TROPICAL_RAIN, WeatherType.THUNDER_STORM -> {
            for (i in 0..40) {
                val rx = (i * 37f + offset * 0.4f) % w
                val ry = (i * 29f + offset * 2.5f) % h
                drawLine(
                    color = Color(0xFF80D8FF).copy(alpha = 0.65f),
                    start = Offset(rx, ry),
                    end = Offset(rx - 8f, ry + 16f),
                    strokeWidth = 1.5f
                )
            }
        }
        WeatherType.GULF_MIST_SNOW -> {
            for (i in 0..30) {
                val sx = (i * 43f + offset * 0.2f) % w
                val sy = (i * 31f + offset * 0.8f) % h
                drawCircle(
                    color = Color.White.copy(alpha = 0.7f),
                    radius = 2.5f,
                    center = Offset(sx, sy)
                )
            }
        }
        WeatherType.SUNNY_GULF -> {
            drawLine(
                color = Color(0xFFFFE082).copy(alpha = 0.15f),
                start = Offset(w * 0.85f, h * 0.15f),
                end = Offset(w * 0.3f, h * 0.9f),
                strokeWidth = 24f
            )
        }
    }
}
