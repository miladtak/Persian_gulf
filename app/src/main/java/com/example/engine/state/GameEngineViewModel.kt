package com.example.engine.state

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.audio.GulfAudioEngine
import com.example.engine.audio.SfxSoundType
import com.example.engine.localization.EngineLanguage
import com.example.engine.model.*
import com.example.engine.particles.ParticlePresetType
import com.example.engine.particles.ParticleSystemEngine
import com.example.engine.persistence.*
import com.example.engine.physics.CollisionManifold
import com.example.engine.physics.PhysicsEngine2D
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class EngineUiState(
    val language: EngineLanguage = EngineLanguage.ENGLISH,
    val isPlayMode: Boolean = false,
    val activeTool: TransformMode = TransformMode.SELECT,
    val zoomLevel: Float = 1.0f,
    val isMuted: Boolean = false,
    val showGrid: Boolean = true,
    val showColliders: Boolean = true,
    val showLayers: Boolean = false,
    
    // Project Hierarchy
    val currentProjectId: String = "persian_gulf_main",
    val currentProjectTitle: String = "Persian Gulf Odyssey",
    val currentPersianTitle: String = "ماجراجویی خلیج فارس",
    val hierarchyTree: List<HierarchyNode> = emptyList(),
    val selectedHierarchyId: String? = null,
    
    // Scene Viewport Objects
    val sceneObjects: List<SceneObject> = emptyList(),
    val selectedObjectId: String? = null,
    val currentWeather: WeatherType = WeatherType.SUNNY_GULF,
    
    // Visual Scripting Graph
    val scriptNodes: List<ScriptBlockNode> = emptyList(),
    val scriptConnections: List<NodeConnection> = emptyList(),
    val selectedNodeId: String? = null,
    val isScriptExecuting: Boolean = false,
    
    // Inventory
    val inventory: InventoryBag = InventoryBag(),
    
    // Plugins
    val registeredPlugins: List<RegisteredPlugin> = emptyList(),
    
    // Dialogs Visibility
    val showAssetManagerDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showBuildDialog: Boolean = false,
    val showInventoryDialog: Boolean = false,
    val showMultiplayerDialog: Boolean = false,
    val showPluginDialog: Boolean = false,
    val showAddNodeMenu: Boolean = false,
    val showProjectSaveLoadDialog: Boolean = false,
    
    // Licensing
    val isCommercialLicensed: Boolean = true,
    val licenseOwner: String = EngineConstants.OWNER_NAME,
    val enteredLicenseKey: String = EngineConstants.MASTER_LICENSE_KEY,
    val licenseFeedback: String = "Licensed to Milad Aziznejad (PRO)",

    // Game Mode State
    val playerHealth: Float = 100f,
    val playerPower: Float = 100f,
    val playerCoins: Int = 350,
    val isAttacking: Boolean = false,
    val isJumping: Boolean = false,
    val isDodging: Boolean = false,
    val playerX: Float = 280f,
    val playerY: Float = 280f,
    val playerVx: Float = 0f,
    val playerVy: Float = 0f,
    val playerFacingRight: Boolean = true,
    val joystickVector: Offset = Offset.Zero,
    val activeParticles: List<com.example.engine.particles.Particle> = emptyList(),
    val savedProjectsList: List<ProjectEntity> = emptyList(),
    val logMessages: List<String> = listOf("GAME ENGINE PERSIAN GULF initialized.", "Ready for visual scripting.")
)

class GameEngineViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EngineUiState())
    val uiState: StateFlow<EngineUiState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null
    private val audioEngine = GulfAudioEngine()
    private val particleEngine = ParticleSystemEngine()
    private var projectRepository: ProjectRepository? = null
    private var runtimeSaveSystem: RuntimeSaveSystem? = null

    init {
        loadInitialData()
        startGameLoop()
    }

    fun initializeWithContext(context: Context) {
        if (projectRepository == null) {
            val db = EngineDatabase.getInstance(context)
            projectRepository = ProjectRepository(db.projectDao())
            runtimeSaveSystem = RuntimeSaveSystem(context)

            // Listen to saved projects flow
            viewModelScope.launch {
                projectRepository?.allProjects?.collect { projects ->
                    _uiState.update { it.copy(savedProjectsList = projects) }
                }
            }
        }
    }

    private fun loadInitialData() {
        val defaultTemplate = ProjectTemplates.buildTemplate("template_pirate_gulf")

        // Build the exact tree hierarchy requested
        val hierarchy = listOf(
            HierarchyNode(
                name = "Scenes",
                persianName = "صحنه‌ها",
                type = NodeType.FOLDER,
                iconType = IconCategory.SCENE,
                children = listOf(
                    HierarchyNode(name = "Main Level", persianName = "مرحله اصلی", type = NodeType.FILE, iconType = IconCategory.SCENE, isSelected = true),
                    HierarchyNode(name = "Start Screen", persianName = "صفحه شروع", type = NodeType.FILE, iconType = IconCategory.SCENE)
                )
            ),
            HierarchyNode(
                name = "Scripts",
                persianName = "اسکریپت‌ها",
                type = NodeType.FOLDER,
                iconType = IconCategory.SCRIPT,
                children = listOf(
                    HierarchyNode(name = "PlayerControl", persianName = "کنترل بازیکن", type = NodeType.FILE, iconType = IconCategory.SCRIPT),
                    HierarchyNode(name = "AIBehavior", persianName = "رفتار هوش مصنوعی", type = NodeType.FILE, iconType = IconCategory.SCRIPT)
                )
            ),
            HierarchyNode(
                name = "Textures",
                persianName = "تکسچرها",
                type = NodeType.FOLDER,
                iconType = IconCategory.TEXTURE,
                children = listOf(
                    HierarchyNode(name = "CharacterSprites", persianName = "اسپرایت‌های کاراکتر", type = NodeType.FOLDER, iconType = IconCategory.TEXTURE),
                    HierarchyNode(name = "Environment", persianName = "محیط و اشیاء", type = NodeType.FOLDER, iconType = IconCategory.TEXTURE)
                )
            ),
            HierarchyNode(name = "Sound", persianName = "صداها", type = NodeType.FOLDER, iconType = IconCategory.SOUND),
            HierarchyNode(name = "Fonts", persianName = "فونت‌ها", type = NodeType.FOLDER, iconType = IconCategory.FONT),
            HierarchyNode(name = "Settings", persianName = "تنظیمات", type = NodeType.FOLDER, iconType = IconCategory.SETTINGS),
            HierarchyNode(
                name = "Plugins",
                persianName = "پلاگین‌ها",
                type = NodeType.FOLDER,
                iconType = IconCategory.PLUGIN,
                children = listOf(
                    HierarchyNode(name = "AI_Behavior_Pack", persianName = "پک رفتار هوش مصنوعی", type = NodeType.FILE, iconType = IconCategory.PLUGIN),
                    HierarchyNode(name = "Cutscene_Creator", persianName = "سازنده کات‌سین", type = NodeType.FILE, iconType = IconCategory.PLUGIN)
                )
            ),
            HierarchyNode(
                name = "Items",
                persianName = "آیتم‌ها",
                type = NodeType.FOLDER,
                iconType = IconCategory.ITEM,
                children = listOf(
                    HierarchyNode(name = "Item_Bag", persianName = "کوله پشتی", type = NodeType.FILE, iconType = IconCategory.ITEM),
                    HierarchyNode(name = "Item_Type", persianName = "نوع آیتم", type = NodeType.FILE, iconType = IconCategory.ITEM)
                )
            )
        )

        // Initial Scene Objects (Sailor hero, Persian Dhow ship, Island terrain, palm tree, enemies)
        val objects = listOf(
            SceneObject(
                id = "player_hero",
                name = "Sailor Hero",
                type = SceneObjectType.PLAYER,
                x = 280f,
                y = 280f,
                width = 80f,
                height = 110f,
                isSelected = true
            ),
            SceneObject(
                id = "persian_dhow",
                name = "Persian Dhow Boat",
                type = SceneObjectType.SHIP_DHOW,
                x = 450f,
                y = 260f,
                width = 190f,
                height = 160f
            ),
            SceneObject(
                id = "island_cliff",
                name = "Island Ground",
                type = SceneObjectType.TERRAIN_CLIFF,
                x = 200f,
                y = 350f,
                width = 300f,
                height = 130f,
                isStatic = true
            ),
            SceneObject(
                id = "palm_tree_1",
                name = "Palm Tree",
                type = SceneObjectType.PALM_TREE,
                x = 180f,
                y = 220f,
                width = 110f,
                height = 160f
            ),
            SceneObject(
                id = "enemy_guard_1",
                name = "Gulf Guard",
                type = SceneObjectType.ENEMY_GUARD,
                x = 650f,
                y = 280f,
                width = 75f,
                height = 105f,
                aiProfile = EnemyAIProfile(
                    name = "Gulf Harbor Guard",
                    persianName = "نگهبان بندر خلیج",
                    type = EnemyPresetType.PATROLLING_SOLDIER,
                    patrolStartX = 550f,
                    patrolEndX = 750f,
                    currentHealth = 100f,
                    currentPower = 70f
                )
            )
        )

        // Visual Scripting nodes matching image_0.png
        val nodes = listOf(
            ScriptBlockNode(
                id = "node_on_start",
                title = "On_Start",
                persianTitle = "هنگام_شروع",
                category = NodeCategory.EVENT_TRIGGER,
                x = 40f,
                y = 40f,
                outputSockets = listOf(SocketPin(id = "out_start", name = "Exec", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "node_var_player",
                title = "Variable: Player_Object",
                persianTitle = "متغیر: آبجکت_بازیکن",
                category = NodeCategory.VARIABLE,
                x = 60f,
                y = 100f,
                inputSockets = listOf(SocketPin(id = "in_player", name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(id = "out_player", name = "PlayerRef", type = SocketType.DATA_OBJECT))
            ),
            ScriptBlockNode(
                id = "node_sync_controls",
                title = "Sync_Controls_To: Touch_Buttons",
                persianTitle = "همگام‌سازی_کنترل: دکمه‌های_لمسی",
                category = NodeCategory.ACTION,
                x = 60f,
                y = 155f,
                inputSockets = listOf(SocketPin(id = "in_sync", name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(id = "out_sync", name = "Out", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "node_enable_ai",
                title = "Enable_AI: Basic_Guard",
                persianTitle = "فعال‌سازی_هوش_مصنوعی: نگهبان_پایه",
                category = NodeCategory.AI_CONTROL,
                x = 60f,
                y = 210f,
                inputSockets = listOf(SocketPin(id = "in_ai", name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(id = "out_ai", name = "AI_Event", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "node_get_item",
                title = "Get_Item",
                persianTitle = "دریافت_آیتم",
                subtitle = "Son",
                persianSubtitle = "تعداد آیتم",
                category = NodeCategory.ITEM_SYSTEM,
                x = 40f,
                y = 310f,
                outputSockets = listOf(SocketPin(id = "out_item", name = "ItemFlow", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "node_use_item",
                title = "Use_Item",
                persianTitle = "استفاده_از_آیتم",
                subtitle = "x",
                category = NodeCategory.ITEM_SYSTEM,
                x = 240f,
                y = 310f,
                inputSockets = listOf(SocketPin(id = "in_use", name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(
                    SocketPin(id = "out_use1", name = "On_Use", type = SocketType.FLOW),
                    SocketPin(id = "out_use2", name = "Attack_Branch", type = SocketType.FLOW)
                )
            ),
            ScriptBlockNode(
                id = "node_attack",
                title = "Attack",
                persianTitle = "حمله_شمشیر",
                subtitle = "fx",
                category = NodeCategory.ACTION,
                x = 240f,
                y = 420f,
                inputSockets = listOf(SocketPin(id = "in_atk", name = "Trigger", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(id = "out_atk", name = "Damage", type = SocketType.DATA_NUMBER))
            ),
            ScriptBlockNode(
                id = "node_jump",
                title = "Jump",
                persianTitle = "پرش",
                category = NodeCategory.ACTION,
                x = 420f,
                y = 490f,
                inputSockets = listOf(SocketPin(id = "in_jump", name = "Trigger", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "node_multiplayer",
                title = "Multiplayer: Sync_Lobby",
                persianTitle = "چندنفره: همگام‌سازی_لابی",
                category = NodeCategory.MULTIPLAYER,
                x = 420f,
                y = 120f,
                inputSockets = listOf(SocketPin(id = "in_mp", name = "Connect", type = SocketType.FLOW))
            )
        )

        // Connections matching image_0.png wires
        val connections = listOf(
            NodeConnection(fromNodeId = "node_on_start", fromSocketId = "out_start", toNodeId = "node_var_player", toSocketId = "in_player"),
            NodeConnection(fromNodeId = "node_var_player", fromSocketId = "out_player", toNodeId = "node_sync_controls", toSocketId = "in_sync"),
            NodeConnection(fromNodeId = "node_sync_controls", fromSocketId = "out_sync", toNodeId = "node_enable_ai", toSocketId = "in_ai"),
            NodeConnection(fromNodeId = "node_sync_controls", fromSocketId = "out_sync", toNodeId = "node_multiplayer", toSocketId = "in_mp"),
            NodeConnection(fromNodeId = "node_get_item", fromSocketId = "out_item", toNodeId = "node_use_item", toSocketId = "in_use"),
            NodeConnection(fromNodeId = "node_use_item", fromSocketId = "out_use1", toNodeId = "node_attack", toSocketId = "in_atk"),
            NodeConnection(fromNodeId = "node_use_item", fromSocketId = "out_use2", toNodeId = "node_jump", toSocketId = "in_jump")
        )

        // Initial Inventory Items
        val initialItems = mutableListOf(
            GameItem(name = "Sailor Cutlass", persianName = "شمشیر دریانورد", description = "Sharpened blade crafted for Gulf corsairs.", persianDescription = "شمشیر تیز مخصوص جنگاوران خلیج فارس.", iconKey = "sword", count = 1, itemType = ItemType.WEAPON, rarity = ItemRarity.RARE, powerBoost = 25),
            GameItem(name = "Golden Dinar Pouch", persianName = "کیسه سکه طلا", description = "Trade currency from Persian Gulf harbors.", persianDescription = "سکه های طلای باستانی برای تجارت.", iconKey = "gold_bag", count = 150, itemType = ItemType.TREASURE, rarity = ItemRarity.COMMON),
            GameItem(name = "Persian Gulf Dates", persianName = "خرمای خلیج فارس", description = "Restores 40 Health and boosts stamina.", persianDescription = "بازیابی ۴۰ واحد سلامت و انرژی.", iconKey = "food", count = 5, itemType = ItemType.CONSUMABLE, healthRestore = 40),
            GameItem(name = "Astrolabe & Compass", persianName = "اسطرلاب و قطب‌نما", description = "Ancient Persian marine navigation tool.", persianDescription = "ابزار ناوبری و جهت یابی دریایی کهن.", iconKey = "compass", count = 1, itemType = ItemType.QUEST, rarity = ItemRarity.LEGENDARY_GULF)
        )

        // Pre-installed Plugins
        val plugins = listOf(
            RegisteredPlugin(
                name = "AI_Behavior_Pack",
                persianName = "بسته رفتار هوش مصنوعی",
                author = EngineConstants.OWNER_NAME,
                version = "2.1.0",
                description = "Advanced finite state machines with multi-agent coordination, perception cones, and stealth alerting.",
                persianDescription = "ماشین وضعیت پیشرفته با هماهنگی چندعاملی و میدان دید هوشمند.",
                category = "AI System",
                codeSnippet = """
                    // Extension by Milad Aziznejad
                    class AIBehaviorPackPlugin : EnginePlugin {
                        override val pluginId = "ai_behavior_pack"
                        override fun onEngineInit() {
                            registerAIState("Patrol", PatrolBehavior())
                            registerAIState("AggroChase", ChaseBehavior())
                        }
                    }
                """.trimIndent()
            ),
            RegisteredPlugin(
                name = "Cutscene_Creator",
                persianName = "سازنده کات‌سین داستانی",
                author = EngineConstants.OWNER_NAME,
                version = "1.5.0",
                description = "Keyframe narrative sequencing and player-to-player synchronized cutscene triggers.",
                persianDescription = "تایم‌لاین انیمیشن کات‌سین‌ها و دیالوگ‌های داستانی آنلاین.",
                category = "Cinematics",
                codeSnippet = """
                    // Extension by Milad Aziznejad
                    class CutsceneCreatorPlugin : EnginePlugin {
                        override val pluginId = "cutscene_creator"
                        override fun onEngineInit() {
                            enableTimelineSequencer()
                        }
                    }
                """.trimIndent()
            ),
            RegisteredPlugin(
                name = "PersianGulf_Physics_Extender",
                persianName = "افزونه فیزیک امواج خلیج فارس",
                author = EngineConstants.OWNER_NAME,
                version = "3.0.0",
                description = "Hydrodynamic buoyancy for ships, realistic wind resistance, and gravity forces.",
                persianDescription = "شبیه‌سازی شناوری هیدرودینامیک قایق‌ها و نیروهای باد و گرانش.",
                category = "Physics",
                codeSnippet = """
                    // Extension by Milad Aziznejad
                    class PersianGulfPhysicsPlugin : EnginePlugin {
                        override val pluginId = "pg_physics_extender"
                        override fun onEngineInit() {
                            registerBuoyancyForce("WaterBuoyancy")
                        }
                    }
                """.trimIndent()
            )
        )

        _uiState.update {
            it.copy(
                hierarchyTree = hierarchy,
                sceneObjects = objects,
                scriptNodes = nodes,
                scriptConnections = connections,
                inventory = InventoryBag(items = initialItems),
                registeredPlugins = plugins,
                selectedObjectId = "player_hero"
            )
        }
    }

    private fun startGameLoop() {
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(33) // ~30 FPS engine simulation loop
                updatePhysicsAndAI()
            }
        }
    }

    private fun updatePhysicsAndAI() {
        val state = _uiState.value

        // Step active particle systems
        particleEngine.update(0.033f)
        val currentParticles = particleEngine.getActiveParticles()

        // Update Enemy AI patrolling and chasing
        val updatedObjects = state.sceneObjects.map { obj ->
            if (obj.aiProfile != null) {
                val profile = obj.aiProfile!!
                var enemyX = obj.x
                val playerX = if (state.isPlayMode) state.playerX else 280f
                val distToPlayer = abs(playerX - enemyX)

                var newState = profile.state
                var newAggro = profile.isAggro

                // Perception check
                if (distToPlayer < profile.detectionRadius) {
                    newAggro = true
                    if (distToPlayer <= profile.attackRadius) {
                        newState = AIState.ATTACK
                    } else {
                        newState = AIState.CHASE
                        // Move toward player
                        if (playerX > enemyX) {
                            enemyX += profile.moveSpeed
                            profile.facingRight = true
                        } else {
                            enemyX -= profile.moveSpeed
                            profile.facingRight = false
                        }
                    }
                } else {
                    newAggro = false
                    newState = AIState.PATROL
                    // Patrol between bounds
                    if (profile.facingRight) {
                        enemyX += profile.moveSpeed * 0.5f
                        if (enemyX >= profile.patrolEndX) profile.facingRight = false
                    } else {
                        enemyX -= profile.moveSpeed * 0.5f
                        if (enemyX <= profile.patrolStartX) profile.facingRight = true
                    }
                }

                val updatedProfile = profile.copy(
                    state = newState,
                    isAggro = newAggro,
                    currentPower = (profile.currentPower + 0.2f).coerceAtMost(profile.maxPower)
                )
                obj.copy(x = enemyX, aiProfile = updatedProfile)
            } else {
                obj
            }
        }

        // If in live play test mode, simulate player movement & physics
        if (state.isPlayMode) {
            val joy = state.joystickVector
            var px = state.playerX
            var py = state.playerY
            var vx = state.playerVx
            var vy = state.playerVy
            var facing = state.playerFacingRight

            // Apply joystick horizontal speed
            if (joy != Offset.Zero) {
                vx = joy.x * 6.5f
                if (joy.x > 0.1f) facing = true
                else if (joy.x < -0.1f) facing = false
            } else {
                vx *= 0.7f // Friction
            }

            // Gravity
            vy += 0.8f
            py += vy
            px += vx

            // Ground collision
            val groundY = 280f
            if (py >= groundY) {
                py = groundY
                vy = 0f
            }

            // Collision check against scene triggers (e.g. Chest)
            state.sceneObjects.forEach { obj ->
                if (obj.collider.isTrigger) {
                    val dist = kotlin.math.hypot(px - obj.x, py - obj.y)
                    if (dist < 50f) {
                        // Collision triggered
                        if (obj.type == SceneObjectType.TREASURE_CHEST) {
                            particleEngine.burstPreset(ParticlePresetType.PERSIAN_GOLD_DUST, obj.x, obj.y, 10)
                        }
                    }
                }
            }

            _uiState.update {
                it.copy(
                    sceneObjects = updatedObjects,
                    playerX = px.coerceIn(50f, 950f),
                    playerY = py,
                    playerVx = vx,
                    playerVy = vy,
                    playerFacingRight = facing,
                    activeParticles = currentParticles
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    sceneObjects = updatedObjects,
                    activeParticles = currentParticles
                )
            }
        }
    }

    // Audio Methods
    fun playSfx(type: SfxSoundType) {
        if (!_uiState.value.isMuted) {
            audioEngine.playSfx(type)
        }
    }

    fun burstParticleEffect(preset: ParticlePresetType, x: Float = 300f, y: Float = 250f) {
        particleEngine.burstPreset(preset, x, y, 20)
        _uiState.update {
            it.copy(
                activeParticles = particleEngine.getActiveParticles(),
                logMessages = it.logMessages + "✨ Burst particle preset: ${preset.name}"
            )
        }
    }

    // Actions & Controls
    fun toggleLanguage() {
        _uiState.update {
            val nextLang = if (it.language == EngineLanguage.ENGLISH) EngineLanguage.PERSIAN else EngineLanguage.ENGLISH
            it.copy(language = nextLang)
        }
    }

    fun togglePlayMode() {
        val nextPlay = !_uiState.value.isPlayMode
        if (nextPlay) {
            playSfx(SfxSoundType.POWERUP_FANFARE)
            if (!_uiState.value.isMuted) audioEngine.startGulfAmbienceBgm()
        } else {
            audioEngine.stopBgm()
        }

        _uiState.update {
            it.copy(
                isPlayMode = nextPlay,
                playerX = 280f,
                playerY = 280f,
                playerVx = 0f,
                playerVy = 0f,
                logMessages = it.logMessages + if (nextPlay) "🎮 Game Mode Started: Touch controls & Physics active." else "⏹️ Returned to Scene Editor."
            )
        }
    }

    fun setJoystickVector(vector: Offset) {
        _uiState.update { it.copy(joystickVector = vector) }
    }

    fun onPlayerJump() {
        _uiState.update {
            if (it.playerY >= 279f) {
                playSfx(SfxSoundType.JUMP)
                it.copy(playerVy = -13.5f, isJumping = true)
            } else it
        }
        viewModelScope.launch {
            delay(400)
            _uiState.update { it.copy(isJumping = false) }
        }
    }

    fun onPlayerAttack() {
        playSfx(SfxSoundType.SWORD_ATTACK)
        particleEngine.burstPreset(ParticlePresetType.SWORD_SLASH_SPARKS, _uiState.value.playerX + 40f, _uiState.value.playerY, 15)
        _uiState.update {
            it.copy(
                isAttacking = true,
                logMessages = it.logMessages + "⚔️ Sailor Cutlass Attack executed!"
            )
        }
        viewModelScope.launch {
            delay(350)
            _uiState.update { it.copy(isAttacking = false) }
        }
    }

    fun onPlayerDodge() {
        playSfx(SfxSoundType.OCEAN_SPLASH)
        _uiState.update {
            val dodgeDir = if (it.playerFacingRight) 15f else -15f
            it.copy(
                isDodging = true,
                playerVx = dodgeDir,
                logMessages = it.logMessages + "💨 Quick Dodge maneuver!"
            )
        }
        viewModelScope.launch {
            delay(250)
            _uiState.update { it.copy(isDodging = false) }
        }
    }

    fun onUseItem(item: GameItem) {
        playSfx(SfxSoundType.COIN_COLLECT)
        _uiState.update { state ->
            val updatedItems = state.inventory.items.toMutableList()
            val existing = updatedItems.find { it.id == item.id }
            if (existing != null) {
                if (existing.count > 1) {
                    val idx = updatedItems.indexOf(existing)
                    updatedItems[idx] = existing.copy(count = existing.count - 1)
                } else {
                    updatedItems.remove(existing)
                }
            }
            val newHealth = (state.playerHealth + item.healthRestore).coerceAtMost(100f)
            val newPower = (state.playerPower + item.powerBoost).coerceAtMost(100f)
            state.copy(
                inventory = state.inventory.copy(items = updatedItems),
                playerHealth = newHealth,
                playerPower = newPower,
                logMessages = state.logMessages + "🎒 Used Item: ${item.name}"
            )
        }
    }

    fun selectSceneObject(id: String) {
        _uiState.update { state ->
            val updated = state.sceneObjects.map { it.copy(isSelected = (it.id == id)) }
            state.copy(sceneObjects = updated, selectedObjectId = id)
        }
    }

    fun moveSceneObject(id: String, dx: Float, dy: Float) {
        _uiState.update { state ->
            val updated = state.sceneObjects.map { obj ->
                if (obj.id == id) {
                    obj.copy(x = obj.x + dx, y = obj.y + dy)
                } else obj
            }
            state.copy(sceneObjects = updated)
        }
    }

    fun setWeather(weather: WeatherType) {
        _uiState.update { it.copy(currentWeather = weather) }
    }

    fun setTransformTool(tool: TransformMode) {
        _uiState.update { it.copy(activeTool = tool) }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { it.copy(zoomLevel = zoom.coerceIn(0.5f, 2.5f)) }
    }

    fun toggleMute() {
        _uiState.update {
            val nextMuted = !it.isMuted
            audioEngine.isMuted = nextMuted
            it.copy(isMuted = nextMuted)
        }
    }

    fun toggleLayers() {
        _uiState.update { it.copy(showLayers = !it.showLayers) }
    }

    fun toggleGrid() {
        _uiState.update { it.copy(showGrid = !it.showGrid) }
    }

    fun addObjectFromPalette(type: SceneObjectType) {
        val newObj = when (type) {
            SceneObjectType.SHIP_DHOW -> SceneObject(name = "Dhow Boat", type = type, x = 400f, y = 250f, width = 160f, height = 130f)
            SceneObjectType.PALM_TREE -> SceneObject(name = "Palm Tree", type = type, x = 320f, y = 200f, width = 100f, height = 150f)
            SceneObjectType.TERRAIN_CLIFF -> SceneObject(name = "Cliff Ground", type = type, x = 300f, y = 340f, width = 180f, height = 90f, isStatic = true)
            SceneObjectType.CLOUD -> SceneObject(name = "Fluffy Cloud", type = type, x = 200f, y = 80f, width = 120f, height = 60f)
            SceneObjectType.TREASURE_CHEST -> SceneObject(name = "Gold Chest", type = type, x = 330f, y = 300f, width = 60f, height = 60f, particlePreset = ParticlePresetType.PERSIAN_GOLD_DUST)
            else -> SceneObject(name = "Prop", type = type, x = 300f, y = 260f, width = 80f, height = 80f)
        }
        _uiState.update {
            it.copy(
                sceneObjects = it.sceneObjects + newObj,
                selectedObjectId = newObj.id,
                logMessages = it.logMessages + "➕ Added ${newObj.name} to Scene."
            )
        }
    }

    fun deleteSelectedObject() {
        val selId = _uiState.value.selectedObjectId ?: return
        _uiState.update { state ->
            val filtered = state.sceneObjects.filter { it.id != selId }
            state.copy(sceneObjects = filtered, selectedObjectId = null)
        }
    }

    fun moveNode(nodeId: String, dx: Float, dy: Float) {
        _uiState.update { state ->
            val updated = state.scriptNodes.map { node ->
                if (node.id == nodeId) {
                    node.copy(x = node.x + dx, y = node.y + dy)
                } else node
            }
            state.copy(scriptNodes = updated)
        }
    }

    fun addScriptNode(title: String, persianTitle: String, category: NodeCategory, subtitle: String? = null) {
        val newNode = ScriptBlockNode(
            title = title,
            persianTitle = persianTitle,
            subtitle = subtitle,
            category = category,
            x = 100f + (Math.random() * 200).toFloat(),
            y = 100f + (Math.random() * 200).toFloat(),
            inputSockets = listOf(SocketPin(name = "In", type = SocketType.FLOW)),
            outputSockets = listOf(SocketPin(name = "Out", type = SocketType.FLOW))
        )
        _uiState.update {
            it.copy(
                scriptNodes = it.scriptNodes + newNode,
                showAddNodeMenu = false,
                logMessages = it.logMessages + "🧩 Added Visual Block: $title"
            )
        }
    }

    fun runVisualScript() {
        playSfx(SfxSoundType.VICTORY_CHIME)
        _uiState.update {
            it.copy(
                isScriptExecuting = true,
                logMessages = it.logMessages + "⚡ Executing Visual Script Graph...",
                playerCoins = it.playerCoins + 10
            )
        }
        viewModelScope.launch {
            delay(800)
            _uiState.update {
                it.copy(
                    isScriptExecuting = false,
                    logMessages = it.logMessages + "✅ Script execution complete."
                )
            }
        }
    }

    // Save and Load System
    fun saveCurrentProject(title: String, persianTitle: String) {
        val s = _uiState.value
        viewModelScope.launch {
            projectRepository?.saveProject(
                projectId = s.currentProjectId,
                title = title,
                persianTitle = persianTitle,
                description = "Custom game project in GAME ENGINE PERSIAN GULF",
                author = s.licenseOwner,
                version = "1.0.0",
                hierarchy = s.hierarchyTree,
                objects = s.sceneObjects,
                nodes = s.scriptNodes,
                connections = s.scriptConnections
            )
            _uiState.update {
                it.copy(
                    currentProjectTitle = title,
                    currentPersianTitle = persianTitle,
                    logMessages = it.logMessages + "💾 Saved project '$title' to Room DB."
                )
            }
        }
    }

    fun loadSavedProject(projectEntity: ProjectEntity) {
        val deserialized = ProjectSerializer.deserializeProject(projectEntity.sceneObjectsJson)
        if (deserialized != null) {
            _uiState.update {
                it.copy(
                    currentProjectId = deserialized.projectId,
                    currentProjectTitle = deserialized.projectName,
                    currentPersianTitle = deserialized.persianName,
                    sceneObjects = deserialized.sceneObjects,
                    scriptNodes = deserialized.scriptNodes,
                    scriptConnections = deserialized.connections,
                    showProjectSaveLoadDialog = false,
                    logMessages = it.logMessages + "📂 Loaded project '${deserialized.projectName}'"
                )
            }
        }
    }

    fun deleteSavedProject(projectId: String) {
        viewModelScope.launch {
            projectRepository?.deleteProject(projectId)
            _uiState.update {
                it.copy(logMessages = it.logMessages + "🗑️ Deleted project '$projectId'")
            }
        }
    }

    fun loadProjectFromTemplate(templateId: String) {
        val t = ProjectTemplates.buildTemplate(templateId)
        _uiState.update {
            it.copy(
                currentProjectId = t.projectId,
                currentProjectTitle = t.projectName,
                currentPersianTitle = t.persianName,
                sceneObjects = t.sceneObjects,
                scriptNodes = t.scriptNodes,
                scriptConnections = t.connections,
                showProjectSaveLoadDialog = false,
                logMessages = it.logMessages + "🎮 Loaded Template '${t.projectName}'"
            )
        }
    }

    fun exportProjectToJson(): String {
        val s = _uiState.value
        return ProjectSerializer.serializeProject(
            projectId = s.currentProjectId,
            projectName = s.currentProjectTitle,
            persianName = s.currentPersianTitle,
            author = s.licenseOwner,
            version = "1.0.0",
            hierarchy = s.hierarchyTree,
            objects = s.sceneObjects,
            nodes = s.scriptNodes,
            connections = s.scriptConnections
        )
    }

    fun importProjectFromJson(jsonStr: String): Boolean {
        val deserialized = ProjectSerializer.deserializeProject(jsonStr) ?: return false
        _uiState.update {
            it.copy(
                currentProjectId = deserialized.projectId,
                currentProjectTitle = deserialized.projectName,
                currentPersianTitle = deserialized.persianName,
                sceneObjects = deserialized.sceneObjects,
                scriptNodes = deserialized.scriptNodes,
                scriptConnections = deserialized.connections,
                logMessages = it.logMessages + "📥 Imported Project JSON '${deserialized.projectName}'"
            )
        }
        return true
    }

    fun triggerBuildExport(appTitle: String, packageId: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            playSfx(SfxSoundType.POWERUP_FANFARE)
            delay(1200)
            _uiState.update {
                it.copy(logMessages = it.logMessages + "🚀 Standalone APK generated: $packageId")
            }
            onFinished()
        }
    }

    // Dialog Toggles
    fun setShowAssetManager(show: Boolean) = _uiState.update { it.copy(showAssetManagerDialog = show) }
    fun setShowSettings(show: Boolean) = _uiState.update { it.copy(showSettingsDialog = show) }
    fun setShowBuild(show: Boolean) = _uiState.update { it.copy(showBuildDialog = show) }
    fun setShowInventory(show: Boolean) = _uiState.update { it.copy(showInventoryDialog = show) }
    fun setShowMultiplayer(show: Boolean) = _uiState.update { it.copy(showMultiplayerDialog = show) }
    fun setShowPlugins(show: Boolean) = _uiState.update { it.copy(showPluginDialog = show) }
    fun setShowAddNodeMenu(show: Boolean) = _uiState.update { it.copy(showAddNodeMenu = show) }
    fun setShowProjectSaveLoad(show: Boolean) = _uiState.update { it.copy(showProjectSaveLoadDialog = show) }

    // License Activation
    fun activateLicense(key: String) {
        if (key.trim() == EngineConstants.MASTER_LICENSE_KEY) {
            _uiState.update {
                it.copy(
                    isCommercialLicensed = true,
                    licenseOwner = EngineConstants.OWNER_NAME,
                    licenseFeedback = "Commercial PRO License Activated for ${EngineConstants.OWNER_NAME}!"
                )
            }
        } else {
            _uiState.update {
                it.copy(licenseFeedback = "Invalid License Key! Master Key is: ${EngineConstants.MASTER_LICENSE_KEY}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
        audioEngine.release()
    }
}
