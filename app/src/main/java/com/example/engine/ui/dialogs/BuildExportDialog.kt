package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.EngineConstants
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun BuildExportDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN
    var isBuilding by remember { mutableStateOf(false) }
    var buildProgress by remember { mutableStateOf(0f) }
    var buildLog by remember { mutableStateOf("Target: Android APK (ARM64 & x86_64)") }
    var targetFormat by remember { mutableStateOf(0) } // 0: Android APK, 1: Standalone JSON Bundle, 2: HTML5 Web

    var appTitleInput by remember { mutableStateOf("Persian Gulf Odyssey") }
    var packageIdInput by remember { mutableStateOf("com.persiangulf.games.odyssey") }
    var versionNameInput by remember { mutableStateOf("1.0.0") }
    var isLandscape by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(true) }

    val clipboard = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfCyan, GulfGold)), RoundedCornerShape(16.dp))
                .testTag("build_export_dialog"),
            color = GulfNavyDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
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
                        Icon(Icons.Default.BuildCircle, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(26.dp))
                        Text(
                            text = if (isRtl) "ساخت و خروجی نهایی بازی (Build & Export)" else "Build & Export Standalone Game",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Target Format Selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { targetFormat = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = if (targetFormat == 0) GulfCyan else GulfNavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = if (targetFormat == 0) GulfNavyDeep else Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Android APK", color = if (targetFormat == 0) GulfNavyDeep else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { targetFormat = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = if (targetFormat == 1) GulfGold else GulfNavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DataObject, contentDescription = null, tint = if (targetFormat == 1) GulfNavyDeep else Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(".gulfproj Bundle", color = if (targetFormat == 1) GulfNavyDeep else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { targetFormat = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = if (targetFormat == 2) Color(0xFF00E676) else GulfNavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = if (targetFormat == 2) GulfNavyDeep else Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HTML5 Web", color = if (targetFormat == 2) GulfNavyDeep else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Config Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Package Config Form
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.4f))))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(if (isRtl) "تنظیمات خروجی بسته برنامه" else "Package & Manifest Configuration", color = GulfGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                            OutlinedTextField(
                                value = appTitleInput,
                                onValueChange = { appTitleInput = it },
                                label = { Text("Game Display Name") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GulfCyan, unfocusedBorderColor = GulfNavyBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = packageIdInput,
                                    onValueChange = { packageIdInput = it },
                                    label = { Text("Application ID") },
                                    modifier = Modifier.weight(2f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GulfCyan, unfocusedBorderColor = GulfNavyBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                                )
                                OutlinedTextField(
                                    value = versionNameInput,
                                    onValueChange = { versionNameInput = it },
                                    label = { Text("Version") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GulfCyan, unfocusedBorderColor = GulfNavyBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isLandscape,
                                        onCheckedChange = { isLandscape = it },
                                        colors = CheckboxDefaults.colors(checkedColor = GulfGold)
                                    )
                                    Text(if (isRtl) "جهت افقی (Landscape)" else "Landscape Mode", color = TextPrimary, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isFullscreen,
                                        onCheckedChange = { isFullscreen = it },
                                        colors = CheckboxDefaults.colors(checkedColor = GulfCyan)
                                    )
                                    Text(if (isRtl) "تمام‌صفحه (Immersive)" else "Fullscreen Mode", color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Build Terminal Log
                    Surface(
                        color = Color(0xFF040A14),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("● $buildLog", color = GulfCyan, fontSize = 11.sp)
                            if (isBuilding) {
                                LinearProgressIndicator(
                                    progress = { buildProgress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = GulfGold,
                                    trackColor = GulfNavyBorder
                                )
                                Text("⚙️ Bundling Scene Entities & Procedural Audio...", color = GulfGold, fontSize = 10.5.sp)
                                Text("📦 Generating signed APK with Master License (${EngineConstants.MASTER_LICENSE_KEY})...", color = Color(0xFF00E676), fontSize = 10.5.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isRtl) "بستن" else "Close", color = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Copy JSON Export Button
                        OutlinedButton(
                            onClick = {
                                val json = viewModel.exportProjectToJson()
                                clipboard.setText(AnnotatedString(json))
                                buildLog = "Copied .gulfproj JSON bundle to clipboard!"
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GulfGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = GulfGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isRtl) "کپی خروجی" else "Export JSON", fontSize = 11.sp)
                        }

                        // Generate / Build Button
                        Button(
                            onClick = {
                                isBuilding = true
                                buildProgress = 0.3f
                                buildLog = "Compiling 2D physics layers & bytecode..."
                                viewModel.triggerBuildExport(appTitleInput, packageIdInput) {
                                    buildProgress = 1.0f
                                    buildLog = "Build Complete! Output: $packageIdInput-release.apk"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GulfCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("start_build_button")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = GulfNavyDeep, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRtl) "تولید APK مستقل" else "Build Standalone APK",
                                color = GulfNavyDeep,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
