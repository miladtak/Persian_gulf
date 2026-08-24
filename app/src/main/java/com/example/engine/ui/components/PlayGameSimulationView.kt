package com.example.engine.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.model.AIState
import com.example.engine.model.SceneObjectType
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PlayGameSimulationView(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN

    val infiniteTransition = rememberInfiniteTransition(label = "GameSimAnimation")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GameWaveOffset"
    )
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GameRainOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GulfNavyDeep)
    ) {
        // 1. World Canvas (Sky, Ocean, Island, Weather)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawSkyAndAtmosphere(w, h, uiState.currentWeather)
            drawPersianGulfOcean(w, h, waveOffset)
            drawIslandTerrain(w, h)
            drawPalmTree(center = Offset(w * 0.22f, h * 0.52f))
            drawWeatherEffects(w, h, uiState.currentWeather, rainOffset)
        }

        // 2. Persian Dhow Ship in Water
        val ship = uiState.sceneObjects.find { it.type == SceneObjectType.SHIP_DHOW }
        if (ship != null) {
            val shipBobbing = (sin(waveOffset * 1.5) * 6f).toFloat()
            Box(
                modifier = Modifier
                    .offset(x = ship.x.dp, y = (ship.y + shipBobbing).dp)
                    .size(ship.width.dp, ship.height.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dhow_ship_sprite_1787576667436),
                    contentDescription = "Persian Dhow Boat",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 3. Enemy AI Characters in Game Mode
        uiState.sceneObjects.filter { it.type == SceneObjectType.ENEMY_GUARD }.forEach { enemy ->
            Box(
                modifier = Modifier
                    .offset(x = enemy.x.dp, y = enemy.y.dp)
                    .size(enemy.width.dp, enemy.height.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Health Bar
                    val hpPercent = (enemy.aiProfile?.currentHealth ?: 100f) / 100f
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(hpPercent)
                                .background(Color(0xFFFF3D00))
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    // Power Bar
                    val pwrPercent = (enemy.aiProfile?.currentPower ?: 70f) / 100f
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(pwrPercent)
                                .background(GulfCyan)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    // Animated Guard Card
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (enemy.aiProfile?.isAggro == true) Color(0xFFB71C1C) else Color(0xFF4A148C))
                            .border(1.2.dp, if (enemy.aiProfile?.isAggro == true) Color(0xFFFF5252) else Color(0xFFBA68C8), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (enemy.aiProfile?.state == AIState.ATTACK) Icons.Default.FlashOn else Icons.Default.Shield,
                            contentDescription = "Enemy Guard",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = if (enemy.aiProfile?.isAggro == true) "⚔️ CHASE" else "🛡️ PATROL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (enemy.aiProfile?.isAggro == true) Color(0xFFFF5252) else GulfGold
                        )
                    )
                }
            }
        }

        // 4. Live Player Character (Sailor Hero) with Physics & Animations
        Box(
            modifier = Modifier
                .offset(x = uiState.playerX.dp, y = uiState.playerY.dp)
                .size(80.dp, 110.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sailor_character_1787576690972),
                contentDescription = "Sailor Player Hero",
                modifier = Modifier.fillMaxSize()
            )

            // Attack Sword Slash FX Animation
            if (uiState.isAttacking) {
                Box(
                    modifier = Modifier
                        .align(if (uiState.playerFacingRight) Alignment.CenterEnd else Alignment.CenterStart)
                        .offset(x = if (uiState.playerFacingRight) 20.dp else (-20).dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GulfCyan.copy(alpha = 0.5f))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Sword Slash",
                        tint = GulfGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Dodge Dust FX
            if (uiState.isDodging) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(30.dp, 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                )
            }
        }

        // 5. Game Top HUD (Player Health, Power, Coins, and Exit Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Stats
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GulfNavyCard)
                        .border(2.dp, GulfGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sailor_character_1787576690972),
                        contentDescription = "Player Avatar",
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                    )
                }

                // Health & Power Bars
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    // Health Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(uiState.playerHealth / 100f)
                                    .background(Color(0xFF00E676))
                            )
                        }
                    }
                    // Power Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("MP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GulfCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(uiState.playerPower / 100f)
                                    .background(GulfCyan)
                            )
                        }
                    }
                }

                // Gold Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GulfNavyDark.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Savings, contentDescription = null, tint = GulfGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${uiState.playerCoins} Dinars", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GulfGold)
                }
            }

            // Exit Game Mode Button
            Button(
                onClick = { viewModel.togglePlayMode() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("exit_play_mode_button")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(EngineStrings.exitPlayMode(lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // 6. Interactive Virtual Joystick on Bottom Left
        var joystickCenter by remember { mutableStateOf(Offset.Zero) }
        var thumbOffset by remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .size(130.dp)
                .clip(CircleShape)
                .background(Color(0xFF0D254C).copy(alpha = 0.65f))
                .border(2.dp, GulfCyan.copy(alpha = 0.7f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            joystickCenter = Offset(65f, 65f)
                            val dx = offset.x - 65f
                            val dy = offset.y - 65f
                            val dist = sqrt(dx * dx + dy * dy)
                            val maxRadius = 45f
                            val clampedDist = dist.coerceAtMost(maxRadius)
                            val angle = atan2(dy, dx)
                            val nx = cos(angle) * clampedDist
                            val ny = sin(angle) * clampedDist
                            thumbOffset = Offset(nx, ny)
                            viewModel.setJoystickVector(Offset(nx / maxRadius, ny / maxRadius))
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val nextThumb = thumbOffset + dragAmount
                            val dist = sqrt(nextThumb.x * nextThumb.x + nextThumb.y * nextThumb.y)
                            val maxRadius = 45f
                            val clampedDist = dist.coerceAtMost(maxRadius)
                            val angle = atan2(nextThumb.y, nextThumb.x)
                            val nx = cos(angle) * clampedDist
                            val ny = sin(angle) * clampedDist
                            thumbOffset = Offset(nx, ny)
                            viewModel.setJoystickVector(Offset(nx / maxRadius, ny / maxRadius))
                        },
                        onDragEnd = {
                            thumbOffset = Offset.Zero
                            viewModel.setJoystickVector(Offset.Zero)
                        },
                        onDragCancel = {
                            thumbOffset = Offset.Zero
                            viewModel.setJoystickVector(Offset.Zero)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Direction Arrows
            Icon(Icons.Default.ArrowDropUp, contentDescription = null, tint = TextSecondary, modifier = Modifier.align(Alignment.TopCenter).size(24.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.align(Alignment.BottomCenter).size(24.dp))
            Icon(Icons.Default.ArrowLeft, contentDescription = null, tint = TextSecondary, modifier = Modifier.align(Alignment.CenterStart).size(24.dp))
            Icon(Icons.Default.ArrowRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.align(Alignment.CenterEnd).size(24.dp))

            // Movable Thumb Stick
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset.x.dp, y = thumbOffset.y.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(GulfCyan, Color(0xFF1E88E5)))
                    )
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }

        // 7. Interactive Action Buttons on Bottom Right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Dodge Button
                LargeGameActionButton(
                    label = EngineStrings.dodge(lang),
                    color = Color(0xFF7E57C2),
                    icon = Icons.Default.FastForward,
                    size = 54
                ) { viewModel.onPlayerDodge() }

                // Jump Button
                LargeGameActionButton(
                    label = EngineStrings.jump(lang),
                    color = Color(0xFF29B6F6),
                    icon = Icons.Default.ArrowUpward,
                    size = 62
                ) { viewModel.onPlayerJump() }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Inventory Bag Button
                LargeGameActionButton(
                    label = EngineStrings.inventory(lang),
                    color = GulfGold,
                    icon = Icons.Default.Inventory2,
                    size = 52
                ) { viewModel.setShowInventory(true) }

                // Attack Button (Sword Slash)
                LargeGameActionButton(
                    label = EngineStrings.attack(lang),
                    color = Color(0xFFE53935),
                    icon = Icons.Default.FlashOn,
                    size = 68
                ) { viewModel.onPlayerAttack() }
            }
        }
    }
}

@Composable
fun LargeGameActionButton(
    label: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.85f))
            .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size((size * 0.42f).dp)
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
