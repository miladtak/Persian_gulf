package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.model.EngineConstants
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.engine.ui.components.IslamicCornerFlourish
import com.example.ui.theme.*

@Composable
fun LicenseSettingsDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    var licenseInput by remember { mutableStateOf(uiState.enteredLicenseKey) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfGold, GulfCyan)), RoundedCornerShape(16.dp)),
            color = GulfNavyDark
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                IslamicCornerFlourish(modifier = Modifier.size(60.dp).align(Alignment.TopEnd))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Title Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = GulfGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = if (lang == EngineLanguage.ENGLISH) "ENGINE LICENSE & SETTINGS" else "مجوز و تنظیمات موتور بازی",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = EngineConstants.ENGINE_NAME,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GulfCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Divider(color = GulfNavyBorder)

                    // Commercial & IP Rights Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfGold.copy(alpha = 0.5f), GulfNavyBorder))),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GulfGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = EngineStrings.licensedTo(lang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GulfGold
                                )
                            }
                            Text(
                                text = "Commercial & IP Owner: ${EngineConstants.OWNER_NAME} (${EngineConstants.OWNER_EMAIL})",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = EngineConstants.COPYRIGHT,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // License Key Input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (lang == EngineLanguage.ENGLISH) "Commercial Activation Key:" else "کلید فعال‌سازی تجاری:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        OutlinedTextField(
                            value = licenseInput,
                            onValueChange = { licenseInput = it },
                            modifier = Modifier.fillMaxWidth().testTag("license_key_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GulfCyan,
                                unfocusedBorderColor = GulfNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            trailingIcon = {
                                Button(
                                    onClick = { viewModel.activateLicense(licenseInput) },
                                    modifier = Modifier.padding(end = 4.dp).height(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GulfCyan),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Activate", color = GulfNavyDeep, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        )
                        Text(
                            text = uiState.licenseFeedback,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.isCommercialLicensed) Color(0xFF00E676) else Color(0xFFFF5252)
                        )
                    }

                    // Engine Version & Configuration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Engine Version: ${EngineConstants.VERSION}", fontSize = 11.sp, color = TextSecondary)
                        Text("Architecture: Native Kotlin + Compose", fontSize = 11.sp, color = GulfCyan)
                    }

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfCyan))),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
