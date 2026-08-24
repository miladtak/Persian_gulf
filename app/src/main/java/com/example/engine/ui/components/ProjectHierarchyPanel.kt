package com.example.engine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.HierarchyNode
import com.example.engine.model.IconCategory
import com.example.engine.model.NodeType
import com.example.engine.model.SceneObjectType
import com.example.engine.model.TransformMode
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun ProjectHierarchyPanel(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN

    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(GulfNavyDark)
            .border(1.dp, GulfNavyBorder)
    ) {
        // Left Column: Tree View File Browser
        Column(
            modifier = Modifier
                .width(185.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == EngineLanguage.ENGLISH) "PROJECT FILES" else "فایل‌های پروژه",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GulfGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                IconButton(
                    onClick = { viewModel.setShowAssetManager(true) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Import Asset",
                        tint = GulfCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Divider(color = GulfNavyBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

            // LazyColumn for Hierarchy Tree
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.hierarchyTree) { node ->
                    HierarchyNodeRow(
                        node = node,
                        lang = lang,
                        depth = 0,
                        selectedId = uiState.selectedHierarchyId,
                        onNodeClick = { clicked ->
                            if (clicked.name.contains("Level") || clicked.name.contains("Screen")) {
                                // Scene click
                            } else if (clicked.name.contains("Item")) {
                                viewModel.setShowInventory(true)
                            } else if (clicked.name.contains("Plugin")) {
                                viewModel.setShowPlugins(true)
                            }
                        }
                    )
                }
            }
        }

        // Vertical divider
        Divider(
            color = GulfNavyBorder,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // Right Strip: Toolbar & Quick Asset Palette (Exact match to image_0.png)
        Column(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .background(GulfNavyDeep)
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Tool: Select / Cursor
            ToolIconButton(
                icon = Icons.Default.NearMe,
                label = "Select",
                isSelected = uiState.activeTool == TransformMode.SELECT,
                onClick = { viewModel.setTransformTool(TransformMode.SELECT) }
            )

            // Tool: Move / Transform Arrows
            ToolIconButton(
                icon = Icons.Default.OpenWith,
                label = "Translate",
                isSelected = uiState.activeTool == TransformMode.TRANSLATE,
                onClick = { viewModel.setTransformTool(TransformMode.TRANSLATE) }
            )

            // Tool: Rect Selection
            ToolIconButton(
                icon = Icons.Default.CropFree,
                label = "Area Select",
                isSelected = uiState.activeTool == TransformMode.BOX_SELECT,
                onClick = { viewModel.setTransformTool(TransformMode.BOX_SELECT) }
            )

            Divider(
                color = GulfNavyBorder,
                modifier = Modifier
                    .width(28.dp)
                    .padding(vertical = 2.dp)
            )

            // Quick Asset Palette: Palm Tree
            PaletteAssetButton(
                icon = Icons.Default.Park,
                label = "Palm Tree",
                color = Color(0xFF4CAF50),
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.PALM_TREE) }
            )

            // Quick Asset Palette: Ship / Boat
            PaletteAssetButton(
                icon = Icons.Default.DirectionsBoat,
                label = "Persian Dhow",
                color = GulfGold,
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.SHIP_DHOW) }
            )

            // Quick Asset Palette: Tree Trunk / Wood
            PaletteAssetButton(
                icon = Icons.Default.Nature,
                label = "Tree Trunk",
                color = Color(0xFF8D6E63),
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.PALM_TREE) }
            )

            // Quick Asset Palette: Ground Tile / Cliff
            PaletteAssetButton(
                icon = Icons.Default.Layers,
                label = "Ground Cliff",
                color = Color(0xFFA1887F),
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.TERRAIN_CLIFF) }
            )

            // Quick Asset Palette: Cloud
            PaletteAssetButton(
                icon = Icons.Default.Cloud,
                label = "Cloud",
                color = Color(0xFFE0F7FA),
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.CLOUD) }
            )

            // Quick Asset Palette: Treasure Bag / Coins
            PaletteAssetButton(
                icon = Icons.Default.Savings,
                label = "Gold Bag",
                color = GulfAmber,
                onClick = { viewModel.addObjectFromPalette(SceneObjectType.TREASURE_CHEST) }
            )
        }
    }
}

@Composable
fun HierarchyNodeRow(
    node: HierarchyNode,
    lang: EngineLanguage,
    depth: Int,
    selectedId: String?,
    onNodeClick: (HierarchyNode) -> Unit
) {
    var isExpanded by remember { mutableStateOf(node.isExpanded) }
    val isSelected = node.isSelected || node.id == selectedId
    val isRtl = lang == EngineLanguage.PERSIAN

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (depth * 10).dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (isSelected) GulfNavyCard else Color.Transparent
                )
                .clickable {
                    if (node.type == NodeType.FOLDER) {
                        isExpanded = !isExpanded
                    }
                    onNodeClick(node)
                }
                .padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder arrow or File bullet
            if (node.type == NodeType.FOLDER) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder,
                    contentDescription = null,
                    tint = GulfGold,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = getIconForCategory(node.iconType),
                    contentDescription = null,
                    tint = if (isSelected) GulfCyan else TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = if (lang == EngineLanguage.ENGLISH) node.name else node.persianName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = if (isSelected) GulfCyan else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                maxLines = 1
            )
        }

        // Render Children
        if (isExpanded && node.children.isNotEmpty()) {
            node.children.forEach { child ->
                HierarchyNodeRow(
                    node = child,
                    lang = lang,
                    depth = depth + 1,
                    selectedId = selectedId,
                    onNodeClick = onNodeClick
                )
            }
        }
    }
}

@Composable
fun ToolIconButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) GulfNavyCard else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) GulfCyan else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GulfCyan else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun PaletteAssetButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(GulfNavyCard.copy(alpha = 0.6f))
            .border(0.8.dp, GulfNavyBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
    }
}

fun getIconForCategory(category: IconCategory): ImageVector {
    return when (category) {
        IconCategory.SCENE -> Icons.Default.Dashboard
        IconCategory.SCRIPT -> Icons.Default.Description
        IconCategory.TEXTURE -> Icons.Default.Image
        IconCategory.SOUND -> Icons.Default.Audiotrack
        IconCategory.FONT -> Icons.Default.FontDownload
        IconCategory.SETTINGS -> Icons.Default.Tune
        IconCategory.PLUGIN -> Icons.Default.Extension
        IconCategory.ITEM -> Icons.Default.Inventory2
        IconCategory.PARTICLES -> Icons.Default.AutoAwesome
        IconCategory.GENERIC_FOLDER -> Icons.Default.Folder
        IconCategory.GENERIC_FILE -> Icons.Default.InsertDriveFile
    }
}
