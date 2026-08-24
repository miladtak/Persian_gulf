package com.example.engine.physics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

enum class ColliderShapeType {
    BOX,
    CIRCLE,
    CAPSULE,
    TRIGGER_ZONE
}

enum class CollisionLayer(val bit: Int) {
    DEFAULT(1 shl 0),
    PLAYER(1 shl 1),
    ENEMY(1 shl 2),
    TERRAIN(1 shl 3),
    PROJECTILE(1 shl 4),
    ITEM_PICKUP(1 shl 5),
    TRIGGER_SENSOR(1 shl 6)
}

data class ColliderDefinition(
    val shape: ColliderShapeType = ColliderShapeType.BOX,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val width: Float = 60f,
    val height: Float = 60f,
    val radius: Float = 30f,
    val isTrigger: Boolean = false,
    val layer: CollisionLayer = CollisionLayer.DEFAULT,
    val mask: Int = 0xFF // Collides with everything by default
) {
    fun getBounds(centerX: Float, centerY: Float): Rect {
        val cx = centerX + offsetX
        val cy = centerY + offsetY
        return when (shape) {
            ColliderShapeType.CIRCLE -> Rect(cx - radius, cy - radius, cx + radius, cy + radius)
            else -> Rect(cx - width / 2f, cy - height / 2f, cx + width / 2f, cy + height / 2f)
        }
    }
}

data class CollisionManifold(
    val entityAId: String,
    val entityBId: String,
    val normal: Offset,
    val penetrationDepth: Float,
    val contactPoint: Offset,
    val isTrigger: Boolean
)

data class RaycastHit(
    val hit: Boolean,
    val hitPoint: Offset = Offset.Zero,
    val distance: Float = 0f,
    val hitEntityId: String? = null
)
