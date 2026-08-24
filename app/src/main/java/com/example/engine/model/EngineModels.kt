package com.example.engine.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.engine.animation.SpriteAnimatorComponent
import com.example.engine.particles.ParticlePresetType
import com.example.engine.physics.ColliderDefinition
import com.example.engine.physics.ColliderShapeType
import java.util.UUID

/**
 * Commercial and Intellectual Property Ownership constants
 * Registered to: Milad Aziznejad
 */
object EngineConstants {
    const val ENGINE_NAME = "GAME ENGINE PERSIAN GULF"
    const val ENGINE_NAME_FA = "موتور بازی خلیج فارس"
    const val OWNER_NAME = "Milad Aziznejad"
    const val OWNER_EMAIL = "MILADTAKTEP@gmail.com"
    const val MASTER_LICENSE_KEY = "55555milad3603"
    const val VERSION = "v2.5.0-PRO"
    const val COPYRIGHT = "© 2026 Milad Aziznejad. All Commercial & IP Rights Reserved."
}

/**
 * File tree item for Project Hierarchy (Left Panel)
 */
data class HierarchyNode(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String,
    val type: NodeType,
    val iconType: IconCategory,
    val isExpanded: Boolean = true,
    val isSelected: Boolean = false,
    val children: List<HierarchyNode> = emptyList(),
    val tag: String? = null
)

enum class NodeType {
    FOLDER, FILE
}

enum class IconCategory {
    SCENE, SCRIPT, TEXTURE, SOUND, FONT, SETTINGS, PLUGIN, ITEM, PARTICLES, GENERIC_FOLDER, GENERIC_FILE
}

/**
 * 2D / 2.5D Scene Entity
 */
data class SceneObject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: SceneObjectType,
    var x: Float,
    var y: Float,
    var rotation: Float = 0f,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var width: Float = 120f,
    var height: Float = 120f,
    var zIndex: Int = 0,
    var isSelected: Boolean = false,
    var isVisible: Boolean = true,
    var isLocked: Boolean = false,
    
    // Physics & Colliders
    var isStatic: Boolean = false,
    var hasPhysics: Boolean = true,
    var mass: Float = 1.0f,
    var friction: Float = 0.5f,
    var restitution: Float = 0.2f,
    var velocityX: Float = 0f,
    var velocityY: Float = 0f,
    var gravityScale: Float = 1.0f,
    var collider: ColliderDefinition = ColliderDefinition(
        shape = ColliderShapeType.BOX,
        width = 100f,
        height = 100f,
        radius = 50f
    ),
    
    // Visual, Animation & Assets
    var spriteResName: String = "sailor",
    var tintColor: Color = Color.White,
    var animator: SpriteAnimatorComponent = SpriteAnimatorComponent(),
    var particlePreset: ParticlePresetType? = null,
    
    // AI configuration if Enemy
    var aiProfile: EnemyAIProfile? = null
) {
    fun getColliderBounds(): Rect {
        return collider.getBounds(x, y)
    }
}

enum class SceneObjectType {
    PLAYER,
    SHIP_DHOW,
    PALM_TREE,
    TERRAIN_CLIFF,
    TERRAIN_WATER,
    CLOUD,
    TREASURE_CHEST,
    ENEMY_GUARD,
    ENEMY_SOLDIER,
    ENEMY_BEAST,
    WEATHER_EMITTER,
    CUSTOM_PROP
}

enum class WeatherType {
    SUNNY_GULF,
    TROPICAL_RAIN,
    GULF_MIST_SNOW,
    THUNDER_STORM
}

enum class TransformMode {
    SELECT,
    TRANSLATE,
    ROTATE,
    SCALE,
    BOX_SELECT
}
