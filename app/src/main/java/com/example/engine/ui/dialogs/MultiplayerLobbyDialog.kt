package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.localization.EngineStrings
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun MultiplayerLobbyDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    var selectedTab by remember { mutableStateOf(0) } // 0: Lobbies, 1: Cutscenes

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfCyan, GulfNavyBorder)), RoundedCornerShape(16.dp)),
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
                        Icon(Icons.Default.Groups, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(24.dp))
                        Text(
                            text = EngineStrings.multiplayerLobby(lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }
                    // Status Pill
                    Surface(
                        color = Color(0xFF00C853).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(Color(0xFF00E676), Color.Transparent)))
                    ) {
                        Text("Online Network Ready", fontSize = 10.sp, color = Color(0xFF00E676), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontWeight = FontWeight.Bold)
                    }
                }

                // Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 0) GulfCyan else GulfNavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lobbies & Matchmaking", color = if (selectedTab == 0) GulfNavyDeep else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { selectedTab = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 1) GulfGold else GulfNavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cutscene & Narrative", color = if (selectedTab == 1) GulfNavyDeep else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = GulfNavyBorder)

                if (selectedTab == 0) {
                    // Multiplayer Lobby Rooms List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LobbyRoomCard(
                            roomName = "Persian Gulf Sea Battle #1",
                            players = "2 / 4",
                            ping = "24ms",
                            status = "Waiting for Players"
                        )
                        LobbyRoomCard(
                            roomName = "Hormuz Island Survival Lair",
                            players = "3 / 4",
                            ping = "38ms",
                            status = "In Game"
                        )
                        LobbyRoomCard(
                            roomName = "Bandar Abbas Trade Guild",
                            players = "1 / 2",
                            ping = "19ms",
                            status = "Ready to Launch"
                        )
                    }
                } else {
                    // Cutscene Timeline & Narratives
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🎬 Scene 1: Arrival at Persian Gulf Harbor", fontWeight = FontWeight.Bold, color = GulfGold, fontSize = 12.sp)
                                Text("Trigger: On_Player_Enter_Zone -> Camera Pan to Dhow Boat -> Play Dialogue: 'Welcome Sailor to Persian Gulf!'", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🎬 Scene 2: Pirate Encounter & Boss Alert", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252), fontSize = 12.sp)
                                Text("Trigger: On_Boss_Spawn -> Lock Controls -> Play Battle Horn FX -> Spawn Guard AI", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // Actions
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

@Composable
fun LobbyRoomCard(roomName: String, players: String, ping: String, status: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.3f)))),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(roomName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.5.sp)
                Text(status, color = TextSecondary, fontSize = 10.5.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(players, fontWeight = FontWeight.Bold, color = GulfGold, fontSize = 11.sp)
                Text(ping, color = Color(0xFF00E676), fontSize = 10.sp)
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = GulfCyan),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Join", color = GulfNavyDeep, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
