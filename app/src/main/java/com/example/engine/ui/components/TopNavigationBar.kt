package com.example.engine.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun TopNavigationBar(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp),
        color = GulfNavyDeep
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IslamicPatternBackground(alpha = 0.05f)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GulfGold, GulfAmber, GulfNavyDark)
                                )
                            )
                            .border(1.5.dp, GulfGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pg_engine_icon_1787576647268),
                            contentDescription = "Persian Gulf Engine Emblem",
                            modifier = Modifier.size(34.dp).clip(CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = EngineStrings.appTitle(lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.1.sp,
                                color = TextPrimary
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "BY MILAD AZIZNEJAD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GulfGold
                                )
                            )
                            Text(
                                text = "• 2D/2.5D NATIVE IDE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = GulfCyan
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Android Engine",
                        tint = GulfGreenAndroid,
                        modifier = Modifier.size(24.dp).padding(start = 4.dp)
                    )
                }

                // Right Menus & Play Mode Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Projects Menu Button
                    TextButton(
                        onClick = { viewModel.setShowProjectSaveLoad(true) },
                        modifier = Modifier.testTag("nav_projects_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Projects",
                            tint = GulfGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "Projects" else "پروژه‌ها",
                            color = GulfGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Language Switcher (EN / فارسی)
                    OutlinedButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("lang_toggle_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (lang == EngineLanguage.PERSIAN) GulfGold else GulfCyan
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.5f)))
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            modifier = Modifier.size(15.dp),
                            tint = if (lang == EngineLanguage.PERSIAN) GulfGold else GulfCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "فارسی" else "English",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Build Menu Button
                    TextButton(
                        onClick = { viewModel.setShowBuild(true) },
                        modifier = Modifier.testTag("nav_build_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handyman,
                            contentDescription = "Build",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = EngineStrings.build(lang),
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Assets Menu Button
                    TextButton(
                        onClick = { viewModel.setShowAssetManager(true) },
                        modifier = Modifier.testTag("nav_assets_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Assets",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = EngineStrings.assets(lang),
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Multiplayer Lobby Menu Button
                    TextButton(
                        onClick = { viewModel.setShowMultiplayer(true) },
                        modifier = Modifier.testTag("nav_multiplayer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Multiplayer",
                            tint = GulfCyan,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "Multiplayer" else "چندنفره",
                            color = GulfCyan,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Plugins Menu Button
                    TextButton(
                        onClick = { viewModel.setShowPlugins(true) },
                        modifier = Modifier.testTag("nav_plugins_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = "Plugins",
                            tint = GulfGold,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "Plugins" else "پلاگین‌ها",
                            color = GulfGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Settings Menu Button (Licensing for Milad Aziznejad)
                    IconButton(
                        onClick = { viewModel.setShowSettings(true) },
                        modifier = Modifier.testTag("nav_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // "حالت بازی" (Play Mode) Glowing Pill Button (matches image_0.png)
                    Button(
                        onClick = { viewModel.togglePlayMode() },
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("play_mode_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isPlayMode) Color(0xFFE53935) else Color(0xFF1E3A8A)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                if (uiState.isPlayMode) listOf(Color(0xFFFF5252), Color(0xFFFF1744))
                                else listOf(GulfCyan, GulfGold)
                            ),
                            width = 1.5.dp
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlayMode) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Play/Stop Mode",
                            tint = if (uiState.isPlayMode) Color.White else GulfCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isPlayMode) EngineStrings.exitPlayMode(lang) else EngineStrings.playMode(lang),
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
