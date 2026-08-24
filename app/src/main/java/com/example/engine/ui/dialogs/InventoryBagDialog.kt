package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.GameItem
import com.example.engine.model.ItemRarity
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun InventoryBagDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    var selectedItem by remember { mutableStateOf(uiState.inventory.items.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfGold, GulfNavyBorder)), RoundedCornerShape(16.dp)),
            color = GulfNavyDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = GulfGold, modifier = Modifier.size(24.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "INVENTORY BAG & ITEM SYSTEM" else "کوله پشتی و سیستم آیتم‌ها",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }

                    // Gold Balance
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GulfNavyCard)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = GulfGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${uiState.inventory.goldCoins} Dinars", fontSize = 11.sp, color = GulfGold, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = GulfNavyBorder)

                // Grid of items & Details column
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.inventory.items) { item ->
                            val isSelected = selectedItem?.id == item.id
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GulfNavyBorder else GulfNavyCard)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) GulfCyan else getRarityColor(item.rarity),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedItem = item },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = getItemIcon(item.iconKey),
                                        contentDescription = item.name,
                                        tint = getRarityColor(item.rarity),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    if (item.count > 1) {
                                        Text(
                                            text = "x${item.count}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right Item Details Box
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.4f))))
                    ) {
                        if (selectedItem != null) {
                            val it = selectedItem!!
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (lang == EngineLanguage.ENGLISH) it.name else it.persianName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = getRarityColor(it.rarity)
                                    )
                                    Text(
                                        text = "Rarity: ${it.rarity.name}",
                                        fontSize = 10.sp,
                                        color = GulfGold
                                    )
                                    Text(
                                        text = if (lang == EngineLanguage.ENGLISH) it.description else it.persianDescription,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.onUseItem(it)
                                            selectedItem = uiState.inventory.items.firstOrNull()
                                        },
                                        modifier = Modifier.weight(1f).height(32.dp).testTag("use_item_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = GulfCyan),
                                        contentPadding = PaddingValues(horizontal = 4.dp)
                                    ) {
                                        Text("Use", color = GulfNavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Select an item", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Close
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }
}

fun getItemIcon(key: String): ImageVector {
    return when (key) {
        "sword" -> Icons.Default.FlashOn
        "gold_bag" -> Icons.Default.Savings
        "food" -> Icons.Default.Restaurant
        "compass" -> Icons.Default.Explore
        else -> Icons.Default.Category
    }
}

fun getRarityColor(rarity: ItemRarity): Color {
    return when (rarity) {
        ItemRarity.COMMON -> Color(0xFFB0BEC5)
        ItemRarity.RARE -> Color(0xFF42A5F5)
        ItemRarity.EPIC -> Color(0xFFAB47BC)
        ItemRarity.LEGENDARY_GULF -> GulfGold
    }
}
