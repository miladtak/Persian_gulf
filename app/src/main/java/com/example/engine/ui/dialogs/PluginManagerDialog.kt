package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.EngineConstants
import com.example.engine.model.RegisteredPlugin
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun PluginManagerDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    var selectedPlugin by remember { mutableStateOf(uiState.registeredPlugins.firstOrNull()) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Icon(Icons.Default.Extension, contentDescription = null, tint = GulfGold, modifier = Modifier.size(24.dp))
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "ENGINE EXTENSION & PLUGINS API" else "پلاگین‌ها و رابط افزونه موتور",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }
                    Text(
                        text = "Author: ${EngineConstants.OWNER_NAME}",
                        fontSize = 11.sp,
                        color = GulfGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = GulfNavyBorder)

                // Plugins list & Code Snippet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Left list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(uiState.registeredPlugins) { plugin ->
                            val isSel = selectedPlugin?.pluginId == plugin.pluginId
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) GulfNavyBorder else GulfNavyCard
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(if (isSel) GulfGold else GulfNavyBorder, Color.Transparent))
                                ),
                                shape = RoundedCornerShape(8.dp),
                                onClick = { selectedPlugin = plugin }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (lang == EngineLanguage.ENGLISH) plugin.name else plugin.persianName,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) GulfGold else Color.White,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${plugin.category} • v${plugin.version}",
                                        fontSize = 10.sp,
                                        color = GulfCyan
                                    )
                                }
                            }
                        }
                    }

                    // Right Code Snippet Box
                    Card(
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF071220)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.4f))))
                    ) {
                        if (selectedPlugin != null) {
                            val p = selectedPlugin!!
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (lang == EngineLanguage.ENGLISH) p.description else p.persianDescription,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Kotlin Extension Definition:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GulfCyan
                                )
                                Surface(
                                    color = Color(0xFF040A12),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().weight(1f)
                                ) {
                                    Text(
                                        text = p.codeSnippet,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF80D8FF),
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Close button
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
