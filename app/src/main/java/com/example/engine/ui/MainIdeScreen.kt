package com.example.engine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.model.EngineConstants
import com.example.engine.state.GameEngineViewModel
import com.example.engine.ui.components.*
import com.example.engine.ui.dialogs.*
import com.example.ui.theme.*

@Composable
fun MainIdeScreen(
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lang = uiState.language

    Surface(
        modifier = modifier.fillMaxSize(),
        color = GulfNavyDeep
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isPlayMode) {
                // Live Play Mode Game View
                PlayGameSimulationView(
                    uiState = uiState,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Complete 3-Panel IDE Layout (Matches image_0.png)
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Top Navigation Bar
                    TopNavigationBar(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Main 3-Panel Workspace
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Left Panel: Project Hierarchy & Quick Palette (230dp)
                        ProjectHierarchyPanel(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = Modifier.width(230.dp)
                        )

                        // Center Panel: Scene Editor Viewport (Weight 1.4)
                        SceneViewportCanvas(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1.4f)
                        )

                        // Right Panel: Visual Scripting Node Editor (Weight 1.2)
                        VisualScriptingPanel(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // 3. Bottom Status Bar (Matches image_0.png bottom bar)
                    BottomStatusBar(
                        uiState = uiState,
                        onBuildClick = { viewModel.setShowBuild(true) },
                        onAssetsClick = { viewModel.setShowAssetManager(true) }
                    )
                }
            }

            // Dialogs & Modals
            if (uiState.showProjectSaveLoadDialog) {
                ProjectSaveLoadDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    savedProjects = uiState.savedProjectsList,
                    onDismiss = { viewModel.setShowProjectSaveLoad(false) }
                )
            }

            if (uiState.showSettingsDialog) {
                LicenseSettingsDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowSettings(false) }
                )
            }

            if (uiState.showInventoryDialog) {
                InventoryBagDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowInventory(false) }
                )
            }

            if (uiState.showMultiplayerDialog) {
                MultiplayerLobbyDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowMultiplayer(false) }
                )
            }

            if (uiState.showPluginDialog) {
                PluginManagerDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowPlugins(false) }
                )
            }

            if (uiState.showAssetManagerDialog) {
                AssetManagerDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowAssetManager(false) }
                )
            }

            if (uiState.showBuildDialog) {
                BuildExportDialog(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowBuild(false) }
                )
            }

            if (uiState.showAddNodeMenu) {
                AddNodeMenuDialog(
                    lang = lang,
                    viewModel = viewModel,
                    onDismiss = { viewModel.setShowAddNodeMenu(false) }
                )
            }
        }
    }
}

@Composable
fun BottomStatusBar(
    uiState: com.example.engine.state.EngineUiState,
    onBuildClick: () -> Unit,
    onAssetsClick: () -> Unit
) {
    val lang = uiState.language

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        color = GulfNavyDeep,
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.3f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left App title in bottom status
            Text(
                text = "${EngineConstants.ENGINE_NAME} • ${uiState.currentProjectTitle}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.5.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )

            // Right Quick actions (Build, Assets, Cyan Build pill button matching image_0.png)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = EngineStrings.build(lang),
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.testTag("bottom_build_text")
                )

                Text(
                    text = EngineStrings.assets(lang),
                    fontSize = 10.sp,
                    color = TextSecondary,
                    modifier = Modifier.testTag("bottom_assets_text")
                )

                // Cyan Filled "Build" Pill button
                Button(
                    onClick = onBuildClick,
                    modifier = Modifier
                        .height(22.dp)
                        .testTag("bottom_build_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GulfCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = EngineStrings.build(lang),
                        color = GulfNavyDeep,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
