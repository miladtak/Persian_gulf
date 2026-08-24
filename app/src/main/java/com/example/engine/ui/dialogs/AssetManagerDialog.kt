package com.example.engine.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.example.engine.audio.SfxSoundType
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.SceneObjectType
import com.example.engine.particles.ParticlePresetType
import com.example.engine.state.EngineUiState
import com.example.engine.state.GameEngineViewModel
import com.example.ui.theme.*

@Composable
fun AssetManagerDialog(
    uiState: EngineUiState,
    viewModel: GameEngineViewModel,
    onDismiss: () -> Unit
) {
    val lang = uiState.language
    val isRtl = lang == EngineLanguage.PERSIAN
    var selectedTab by remember { mutableStateOf(0) } // 0: Textures & Slicer, 1: Audio Synthesizer, 2: Particle FX, 3: Fonts

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Brush.linearGradient(listOf(GulfCyan, GulfGold)), RoundedCornerShape(16.dp))
                .testTag("asset_manager_dialog"),
            color = GulfNavyDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(24.dp))
                        Text(
                            text = if (isRtl) "مدیریت دارایی‌ها، صدا و ذرات" else "Asset Manager & Synthesizer",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = GulfNavyDeep,
                    contentColor = GulfCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isRtl) "تکسچر و اسپرایت" else "Textures", fontSize = 11.sp) },
                        icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(15.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isRtl) "سینتی‌سایزر صدا" else "Sound SFX", fontSize = 11.sp) },
                        icon = { Icon(Icons.Default.Audiotrack, contentDescription = null, modifier = Modifier.size(15.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (isRtl) "سیستم ذرات FX" else "Particles", fontSize = 11.sp) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text(if (isRtl) "فونت‌ها" else "Fonts", fontSize = 11.sp) },
                        icon = { Icon(Icons.Default.FontDownload, contentDescription = null, modifier = Modifier.size(15.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> TexturesAndSlicerTab(viewModel = viewModel, isRtl = isRtl)
                        1 -> SoundSynthesizerTab(viewModel = viewModel, isRtl = isRtl)
                        2 -> ParticleFxTab(viewModel = viewModel, isRtl = isRtl)
                        3 -> FontsTab(isRtl = isRtl)
                    }
                }
            }
        }
    }
}

@Composable
private fun TexturesAndSlicerTab(viewModel: GameEngineViewModel, isRtl: Boolean) {
    var sliceRows by remember { mutableStateOf(4) }
    var sliceCols by remember { mutableStateOf(4) }
    var frameRate by remember { mutableStateOf(8) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sprite Slicer Header Card
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = GulfNavyDeep,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.4f))))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (isRtl) "برش‌دهنده اسپرایت‌شیت (Sprite Sheet Slicer)" else "Sprite Sheet Slicer & Atlas",
                    color = GulfCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Grid: ${sliceCols}x${sliceRows} (${sliceCols * sliceRows} Frames)", color = TextPrimary, fontSize = 11.sp)
                    Text(text = "FPS: $frameRate", color = GulfGold, fontSize = 11.sp)
                    Button(
                        onClick = { viewModel.addObjectFromPalette(SceneObjectType.SHIP_DHOW) },
                        colors = ButtonDefaults.buttonColors(containerColor = GulfNavyLight),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRtl) "افزودن اسپرایت" else "Add Sprite", fontSize = 10.sp)
                    }
                }
            }
        }

        // Pre-loaded Texture Assets Grid
        val textures = listOf(
            Triple("sailor_hero.png", "Character 4x4 Sheet", SceneObjectType.PLAYER),
            Triple("persian_dhow_boat.png", "Ship & Sail", SceneObjectType.SHIP_DHOW),
            Triple("palm_tree_asset.png", "Nature Prop", SceneObjectType.PALM_TREE),
            Triple("harbor_cliff_tile.png", "Terrain Block", SceneObjectType.TERRAIN_CLIFF),
            Triple("gold_treasure_chest.png", "Treasure & Coins", SceneObjectType.TREASURE_CHEST),
            Triple("gulf_guard_soldier.png", "Enemy Guard", SceneObjectType.ENEMY_GUARD)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(textures) { (name, desc, type) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfNavyLight)))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(desc, color = TextSecondary, fontSize = 9.sp)
                        }
                        IconButton(
                            onClick = { viewModel.addObjectFromPalette(type) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = GulfGold, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundSynthesizerTab(viewModel: GameEngineViewModel, isRtl: Boolean) {
    val soundButtons = listOf(
        Pair(SfxSoundType.JUMP, if (isRtl) "پرش (Jump)" else "Jump SFX"),
        Pair(SfxSoundType.COIN_COLLECT, if (isRtl) "سکه طلا (Coin)" else "Coin Collect"),
        Pair(SfxSoundType.SWORD_ATTACK, if (isRtl) "ضربه شمشیر (Sword)" else "Sword Slash"),
        Pair(SfxSoundType.EXPLOSION, if (isRtl) "انفجار (Explosion)" else "Explosion"),
        Pair(SfxSoundType.HIT_DAMAGE, if (isRtl) "آسیب دیدن (Hit)" else "Hit Damage"),
        Pair(SfxSoundType.POWERUP_FANFARE, if (isRtl) "پاورآپ (Powerup)" else "Powerup"),
        Pair(SfxSoundType.LASER_FIRE, if (isRtl) "شلیک لیزر/تیر" else "Laser Fire"),
        Pair(SfxSoundType.OCEAN_SPLASH, if (isRtl) "امواج خلیج فارس" else "Gulf Waves"),
        Pair(SfxSoundType.VICTORY_CHIME, if (isRtl) "پیروزی (Victory)" else "Victory Chime")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GulfNavyDeep,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isRtl) "موتور تولید فرکانس صوتی در زمان واقعی" else "Real-time Procedural Audio Synthesizer",
                        color = GulfGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = if (isRtl) "تولید امواج صوتی Sine, Square, Triangle بدون نیاز به فایل‌های حجیم" else "Generates dynamic chiptune & ambient waves via raw PCM",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { viewModel.playSfx(SfxSoundType.POWERUP_FANFARE) },
                    colors = ButtonDefaults.buttonColors(containerColor = GulfGold),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GulfNavyDeep, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", color = GulfNavyDeep, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(soundButtons) { (sfx, title) ->
                Button(
                    onClick = { viewModel.playSfx(sfx) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.5f)))),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GulfGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = title, color = TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParticleFxTab(viewModel: GameEngineViewModel, isRtl: Boolean) {
    val presets = listOf(
        Pair(ParticlePresetType.PERSIAN_GOLD_DUST, if (isRtl) "غبار طلای خلیج فارس" else "Persian Gold Dust"),
        Pair(ParticlePresetType.GULF_WATER_SPLASH, if (isRtl) "پاشش آب دریای جنوب" else "Gulf Water Splash"),
        Pair(ParticlePresetType.MAGIC_SPARKS, if (isRtl) "جرقه‌های جادویی" else "Magic Sparks"),
        Pair(ParticlePresetType.FIRE_TORCH, if (isRtl) "مشعل آتش" else "Torch Fire"),
        Pair(ParticlePresetType.EXPLOSION_BURST, if (isRtl) "انفجار بمب و توپ" else "Explosion Burst"),
        Pair(ParticlePresetType.RAIN_DROPS, if (isRtl) "باران استوایی خلیج" else "Tropical Rain"),
        Pair(ParticlePresetType.DESERT_SANDSTORM, if (isRtl) "طوفان شن کویر" else "Desert Sandstorm"),
        Pair(ParticlePresetType.SWORD_SLASH_SPARKS, if (isRtl) "جرقه‌های ضربه شمشیر" else "Cutlass Sparks")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = GulfNavyDeep,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRtl) "پیش‌نمایش و آزمایش ذرات در صحنه" else "Test & Burst Particle Emitters",
                    color = GulfCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                Text(
                    text = if (isRtl) "روی هر افکت بزنید تا در صحنه شلیک شود" else "Click to burst in viewport",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presets) { (preset, label) ->
                Button(
                    onClick = { viewModel.burstParticleEffect(preset) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GulfNavyCard),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfCyan.copy(alpha = 0.5f)))),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GulfCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FontsTab(isRtl: Boolean) {
    val fonts = listOf(
        Pair("Vazirmatn (وزیرمتن)", "فونت پیش‌فرض فارسی با طراحی مدرن و خوانا"),
        Pair("Gulf Display", "فونت نمایشی عناوین با سبک هندسی خلیج فارس"),
        Pair("Pixel Retro 8-bit", "Pixel art font suitable for retro 2D arcade games"),
        Pair("Cinzel Decorative", "Classical serif for quests, titles, and ancient lore")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(fonts) { (name, desc) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = GulfNavyCard),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GulfNavyBorder, GulfGold.copy(alpha = 0.3f))))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FontDownload, contentDescription = null, tint = GulfGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(desc, color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
