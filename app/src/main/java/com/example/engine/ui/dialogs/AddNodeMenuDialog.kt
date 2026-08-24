package com.example.engine.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.NodeCategory
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun AddNodeMenuDialog(
    lang: EngineLanguage,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val isRtl = lang == EngineLanguage.PERSIAN
    val presets = listOf(
        // Audio Engine
        NodePreset("Play_SFX: Coin_Chime", "پخش_صدا: سکه_طلا", NodeCategory.AUDIO_ENGINE, "Plays synthesized coin chime"),
        NodePreset("Play_BGM: Gulf_Waves", "پخش_موزیک: امواج_خلیج", NodeCategory.AUDIO_ENGINE, "Streams ambient Persian Gulf soundtrack"),
        NodePreset("Stop_All_Audio", "توقف_همه_صداها", NodeCategory.AUDIO_ENGINE, "Stops BGM & SFX channels"),

        // Physics & Collisions
        NodePreset("On_Collision_Enter", "هنگام_شروع_برخورد", NodeCategory.PHYSICS_COLLISION, "Fires when solid collider contacts another"),
        NodePreset("On_Trigger_Enter", "هنگام_ورود_به_محرک", NodeCategory.PHYSICS_COLLISION, "Fires when entering trigger zone"),
        NodePreset("Add_Force_2D", "اعمال_نیروی_فیزیکی", NodeCategory.PHYSICS_COLLISION, "Pushes body with impulse vector"),
        NodePreset("Raycast_2D", "پرتاب_اشعه_لیزری", NodeCategory.PHYSICS_COLLISION, "Casts line for line-of-sight check"),

        // Animation System
        NodePreset("Play_Animation: Attack", "پخش_انیمیشن: حمله", NodeCategory.ANIMATION_SYSTEM, "Plays attack sprite frames"),
        NodePreset("Play_Animation: Walk", "پخش_انیمیشن: راه_رفتن", NodeCategory.ANIMATION_SYSTEM, "Loops walk sprite frames"),
        NodePreset("Stop_Animation", "توقف_انیمیشن", NodeCategory.ANIMATION_SYSTEM, "Freezes at current frame"),

        // Particle FX
        NodePreset("Burst_Particles: Gold_Dust", "پرتاب_ذرات: غبار_طلا", NodeCategory.PARTICLE_FX, "Emits Persian gold glitter"),
        NodePreset("Burst_Particles: Explosion", "پرتاب_ذرات: انفجار", NodeCategory.PARTICLE_FX, "Spawns fire & smoke burst"),
        NodePreset("Burst_Particles: Water_Splash", "پرتاب_ذرات: پاشش_آب", NodeCategory.PARTICLE_FX, "Emits gulf water droplets"),

        // Persistence & Save Data
        NodePreset("Save_Game_Data: HighScore", "ذخیره_داده_بازی: رکورد", NodeCategory.SAVE_LOAD_DATA, "Persists score key to storage"),
        NodePreset("Load_Game_Data: Checkpoint", "بارگذاری_داده_بازی: چک‌پوینت", NodeCategory.SAVE_LOAD_DATA, "Reads saved checkpoint state"),

        // Triggers, Actions & Logic
        NodePreset("On_Start", "هنگام_شروع", NodeCategory.EVENT_TRIGGER, "Engine lifecycle init"),
        NodePreset("On_Touch_Tap", "هنگام_لمس_صفحه", NodeCategory.EVENT_TRIGGER, "Touch gesture trigger"),
        NodePreset("Move_Player", "حرکت_کاراکتر", NodeCategory.ACTION, "Apply velocity & walk"),
        NodePreset("Dodge_Roll", "حرکت_جاخالی", NodeCategory.ACTION, "Fast dash maneuver"),
        NodePreset("Spawn_Enemy", "ایجاد_دشمن", NodeCategory.AI_CONTROL, "Instantiate soldier AI"),
        NodePreset("Set_Gulf_Weather", "تنظیم_آب_و_هوا", NodeCategory.WEATHER, "Rain, Snow, Storm"),
        NodePreset("If_Condition", "شرط_اگر", NodeCategory.LOGIC, "Boolean branch"),
        NodePreset("Loop_Repeat", "حلقه_تکرار", NodeCategory.LOGIC, "Iterate count"),
        NodePreset("Multiplayer_Lobby", "لابی_چندنفره", NodeCategory.MULTIPLAYER, "Join match")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfCyan, GulfGold)), RoundedCornerShape(16.dp)),
            color = GulfNavyDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (lang == EngineLanguage.ENGLISH) "ADD VISUAL SCRIPT NODE" else "افزودن بلوک اسکریپت تصویری",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = GulfCyan
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Divider(color = GulfNavyBorder)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(presets) { preset ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(
                                    when (preset.category) {
                                        NodeCategory.AUDIO_ENGINE -> listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.5f))
                                        NodeCategory.PHYSICS_COLLISION -> listOf(GulfNavyBorder, Color(0xFF00E676).copy(alpha = 0.5f))
                                        NodeCategory.PARTICLE_FX -> listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.5f))
                                        NodeCategory.ANIMATION_SYSTEM -> listOf(GulfNavyBorder, Color(0xFFE040FB).copy(alpha = 0.5f))
                                        NodeCategory.SAVE_LOAD_DATA -> listOf(GulfNavyBorder, Color(0xFFFF9100).copy(alpha = 0.5f))
                                        else -> listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.3f))
                                    }
                                )
                            ),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                viewModel.addScriptNode(preset.title, preset.persianTitle, preset.category, preset.desc)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == EngineLanguage.ENGLISH) preset.title else preset.persianTitle,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                    Text(preset.desc, color = TextSecondary, fontSize = 10.sp)
                                }
                                Surface(
                                    color = GulfNavyDeep,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        preset.category.name.replace("_", " "),
                                        fontSize = 8.5.sp,
                                        color = GulfGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard)) {
                        Text(if (isRtl) "بستن" else "Close", color = Color.White)
                    }
                }
            }
        }
    }
}

data class NodePreset(val title: String, val persianTitle: String, val category: NodeCategory, val desc: String)
