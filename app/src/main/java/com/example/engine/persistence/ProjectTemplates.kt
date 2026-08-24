package com.example.engine.persistence

import androidx.compose.ui.graphics.Color
import com.example.engine.model.*
import com.example.engine.particles.ParticlePresetType
import com.example.engine.physics.ColliderDefinition
import com.example.engine.physics.ColliderShapeType
import com.example.engine.physics.CollisionLayer

object ProjectTemplates {

    fun getStarterTemplates(): List<TemplateProjectData> {
        return listOf(
            TemplateProjectData(
                id = "template_pirate_gulf",
                title = "Persian Gulf Pirate Quest",
                persianTitle = "ماجراجویی دزدان دریایی خلیج فارس",
                description = "Action-Adventure game with sailor hero, Dhow ship, guard enemies, cutlass attack, and coin collecting.",
                persianDescription = "بازی اکشن-ماجرایی با کاراکتر دریانورد، کشتی لنج بادبانی، نگهبانان بندر و شمشیرزنی.",
                iconKey = "dhow",
                genre = "Action-Adventure"
            ),
            TemplateProjectData(
                id = "template_dhow_runner",
                title = "Dhow Island Runner",
                persianTitle = "سکوبازی دونده جزایر خلیج",
                description = "2D Physics Platformer with jumping mechanics, moving platforms, collectible dates, and ocean obstacles.",
                persianDescription = "سکوبازی دو بعدی با مکانیک پرش فیزیکی، سکوهای متحرک و موانع دریایی.",
                iconKey = "runner",
                genre = "2D Platformer"
            ),
            TemplateProjectData(
                id = "template_hormuz_defense",
                title = "Strait of Hormuz Defense",
                persianTitle = "دفاع از تنگه هرمز",
                description = "Space & Naval Shooter with projectile cannons, particle explosions, waves of enemies, and score tracking.",
                persianDescription = "بازی شوتر هوایی و دریایی با شلیک توپخانه، انفجارهای ذرات و شمارش امتیاز.",
                iconKey = "shooter",
                genre = "Arcade Shooter"
            ),
            TemplateProjectData(
                id = "template_sindbad_rpg",
                title = "Sindbad Ancient Legend RPG",
                persianTitle = "افسانه کهن سندباد RPG",
                description = "Top-down RPG dungeon crawler with inventory equipment, dialog cutscenes, astrolabe quests, and NPC dialogues.",
                persianDescription = "بازی نقش‌آفرینی از بالا به پایین با کوله‌پشتی آیتم‌ها، ماموریت‌های اسطرلاب و دیالوگ‌ها.",
                iconKey = "rpg",
                genre = "Top-Down RPG"
            ),
            TemplateProjectData(
                id = "template_physics_sandbox",
                title = "Gulf Waves & Physics Sandbox",
                persianTitle = "محیط آزمایشی فیزیک و ذرات خلیج",
                description = "Interactive physics playground with bounciness, buoyancy, gravity modifiers, and particle emitters.",
                persianDescription = "آزمایشگاه فیزیک با قابلیت تغییر گرانش، شناوری آب و پرتاب ذرات آتش و طلا.",
                iconKey = "physics",
                genre = "Physics Sandbox"
            )
        )
    }

    fun buildTemplate(templateId: String): DeserializedProject {
        return when (templateId) {
            "template_dhow_runner" -> createPlatformerTemplate()
            "template_hormuz_defense" -> createShooterTemplate()
            "template_sindbad_rpg" -> createRpgTemplate()
            "template_physics_sandbox" -> createPhysicsSandboxTemplate()
            else -> createPirateAdventureTemplate()
        }
    }

    private fun createPirateAdventureTemplate(): DeserializedProject {
        val objects = listOf(
            SceneObject(
                id = "hero",
                name = "Sailor Hero",
                type = SceneObjectType.PLAYER,
                x = 280f,
                y = 280f,
                width = 80f,
                height = 110f,
                isSelected = true,
                collider = ColliderDefinition(
                    shape = ColliderShapeType.BOX,
                    width = 60f,
                    height = 100f,
                    layer = CollisionLayer.PLAYER
                )
            ),
            SceneObject(
                id = "dhow_boat",
                name = "Persian Dhow Boat",
                type = SceneObjectType.SHIP_DHOW,
                x = 450f,
                y = 260f,
                width = 190f,
                height = 160f,
                collider = ColliderDefinition(
                    shape = ColliderShapeType.BOX,
                    width = 160f,
                    height = 120f,
                    layer = CollisionLayer.TERRAIN
                )
            ),
            SceneObject(
                id = "ground",
                name = "Harbor Pier",
                type = SceneObjectType.TERRAIN_CLIFF,
                x = 200f,
                y = 350f,
                width = 320f,
                height = 130f,
                isStatic = true,
                collider = ColliderDefinition(
                    shape = ColliderShapeType.BOX,
                    width = 320f,
                    height = 130f,
                    layer = CollisionLayer.TERRAIN
                )
            ),
            SceneObject(
                id = "gold_chest",
                name = "Treasure Chest",
                type = SceneObjectType.TREASURE_CHEST,
                x = 380f,
                y = 300f,
                width = 60f,
                height = 60f,
                isStatic = true,
                particlePreset = ParticlePresetType.PERSIAN_GOLD_DUST,
                collider = ColliderDefinition(
                    shape = ColliderShapeType.BOX,
                    width = 50f,
                    height = 50f,
                    isTrigger = true,
                    layer = CollisionLayer.ITEM_PICKUP
                )
            )
        )

        val nodes = listOf(
            ScriptBlockNode(
                id = "start",
                title = "On_Start",
                persianTitle = "هنگام_شروع",
                category = NodeCategory.EVENT_TRIGGER,
                x = 40f,
                y = 40f,
                outputSockets = listOf(SocketPin(name = "Exec", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "play_bgm",
                title = "Play_BGM: Gulf_Ambience",
                persianTitle = "پخش_موزیک: امواج_خلیج_فارس",
                category = NodeCategory.AUDIO_ENGINE,
                x = 40f,
                y = 110f,
                inputSockets = listOf(SocketPin(name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(name = "Out", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "on_collide",
                title = "On_Trigger_Enter: Treasure_Chest",
                persianTitle = "هنگام_ورود_به_محرک: صندوق_طلا",
                category = NodeCategory.PHYSICS_COLLISION,
                x = 40f,
                y = 230f,
                outputSockets = listOf(SocketPin(name = "Triggered", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "play_coin_sfx",
                title = "Play_SFX: Coin_Chime",
                persianTitle = "پخش_صدا: سکه_طلا",
                category = NodeCategory.AUDIO_ENGINE,
                x = 240f,
                y = 230f,
                inputSockets = listOf(SocketPin(name = "In", type = SocketType.FLOW)),
                outputSockets = listOf(SocketPin(name = "Out", type = SocketType.FLOW))
            ),
            ScriptBlockNode(
                id = "burst_gold",
                title = "Burst_Particles: Persian_Gold",
                persianTitle = "پرتاب_ذرات: غبار_طلای_خلیج",
                category = NodeCategory.PARTICLE_FX,
                x = 440f,
                y = 230f,
                inputSockets = listOf(SocketPin(name = "In", type = SocketType.FLOW))
            )
        )

        val connections = listOf(
            NodeConnection(fromNodeId = "start", fromSocketId = "out_start", toNodeId = "play_bgm", toSocketId = "in_bgm"),
            NodeConnection(fromNodeId = "on_collide", fromSocketId = "out_trig", toNodeId = "play_coin_sfx", toSocketId = "in_sfx"),
            NodeConnection(fromNodeId = "play_coin_sfx", fromSocketId = "out_sfx", toNodeId = "burst_gold", toSocketId = "in_burst")
        )

        return DeserializedProject(
            projectId = "template_pirate_gulf",
            projectName = "Persian Gulf Pirate Quest",
            persianName = "ماجراجویی دزدان دریایی خلیج فارس",
            author = "Milad Aziznejad",
            version = "1.0.0",
            hierarchy = emptyList(),
            sceneObjects = objects,
            scriptNodes = nodes,
            connections = connections
        )
    }

    private fun createPlatformerTemplate(): DeserializedProject {
        val objects = listOf(
            SceneObject(id = "player", name = "Runner", type = SceneObjectType.PLAYER, x = 120f, y = 280f, width = 70f, height = 95f),
            SceneObject(id = "plat1", name = "Ground 1", type = SceneObjectType.TERRAIN_CLIFF, x = 120f, y = 360f, width = 200f, height = 80f, isStatic = true),
            SceneObject(id = "plat2", name = "Island Floating", type = SceneObjectType.TERRAIN_CLIFF, x = 360f, y = 260f, width = 160f, height = 60f, isStatic = true),
            SceneObject(id = "plat3", name = "Dhow Mast", type = SceneObjectType.SHIP_DHOW, x = 600f, y = 290f, width = 180f, height = 140f, isStatic = true)
        )
        return DeserializedProject(
            projectId = "template_dhow_runner",
            projectName = "Dhow Island Runner",
            persianName = "سکوبازی دونده جزایر خلیج",
            author = "Milad Aziznejad",
            version = "1.0.0",
            hierarchy = emptyList(),
            sceneObjects = objects,
            scriptNodes = emptyList(),
            connections = emptyList()
        )
    }

    private fun createShooterTemplate(): DeserializedProject {
        val objects = listOf(
            SceneObject(id = "battleship", name = "Gulf Flagship", type = SceneObjectType.SHIP_DHOW, x = 200f, y = 200f, width = 150f, height = 120f),
            SceneObject(id = "enemy1", name = "Rival Raider", type = SceneObjectType.ENEMY_GUARD, x = 600f, y = 160f, width = 80f, height = 90f),
            SceneObject(id = "enemy2", name = "Patrol Cruiser", type = SceneObjectType.ENEMY_SOLDIER, x = 650f, y = 280f, width = 80f, height = 90f)
        )
        return DeserializedProject(
            projectId = "template_hormuz_defense",
            projectName = "Strait of Hormuz Defense",
            persianName = "دفاع از تنگه هرمز",
            author = "Milad Aziznejad",
            version = "1.0.0",
            hierarchy = emptyList(),
            sceneObjects = objects,
            scriptNodes = emptyList(),
            connections = emptyList()
        )
    }

    private fun createRpgTemplate(): DeserializedProject {
        val objects = listOf(
            SceneObject(id = "sindbad", name = "Sindbad", type = SceneObjectType.PLAYER, x = 300f, y = 260f, width = 80f, height = 100f),
            SceneObject(id = "chest", name = "Ancient Astrolabe Cache", type = SceneObjectType.TREASURE_CHEST, x = 450f, y = 260f, width = 70f, height = 70f, isStatic = true),
            SceneObject(id = "guard", name = "Harbor Master", type = SceneObjectType.ENEMY_GUARD, x = 600f, y = 260f, width = 80f, height = 110f, isStatic = true)
        )
        return DeserializedProject(
            projectId = "template_sindbad_rpg",
            projectName = "Sindbad Ancient Legend RPG",
            persianName = "افسانه کهن سندباد RPG",
            author = "Milad Aziznejad",
            version = "1.0.0",
            hierarchy = emptyList(),
            sceneObjects = objects,
            scriptNodes = emptyList(),
            connections = emptyList()
        )
    }

    private fun createPhysicsSandboxTemplate(): DeserializedProject {
        val objects = listOf(
            SceneObject(id = "ball1", name = "Bouncy Sphere", type = SceneObjectType.CUSTOM_PROP, x = 250f, y = 100f, width = 70f, height = 70f, restitution = 0.85f, mass = 2f),
            SceneObject(id = "dhow_float", name = "Buoyant Dhow", type = SceneObjectType.SHIP_DHOW, x = 450f, y = 200f, width = 180f, height = 140f, restitution = 0.4f),
            SceneObject(id = "floor", name = "Solid Bedrock", type = SceneObjectType.TERRAIN_CLIFF, x = 300f, y = 360f, width = 450f, height = 90f, isStatic = true)
        )
        return DeserializedProject(
            projectId = "template_physics_sandbox",
            projectName = "Gulf Waves & Physics Sandbox",
            persianName = "محیط آزمایشی فیزیک و ذرات خلیج",
            author = "Milad Aziznejad",
            version = "1.0.0",
            hierarchy = emptyList(),
            sceneObjects = objects,
            scriptNodes = emptyList(),
            connections = emptyList()
        )
    }
}

data class TemplateProjectData(
    val id: String,
    val title: String,
    val persianTitle: String,
    val description: String,
    val persianDescription: String,
    val iconKey: String,
    val genre: String
)
