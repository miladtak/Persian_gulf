package com.example.engine.model

import java.util.UUID

/**
 * Advanced Enemy AI Behavior System
 */
data class EnemyAIProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String,
    val type: EnemyPresetType,
    var state: AIState = AIState.PATROL,
    var currentHealth: Float = 100f,
    var maxHealth: Float = 100f,
    var currentPower: Float = 60f,
    var maxPower: Float = 100f,
    var patrolStartX: Float = 200f,
    var patrolEndX: Float = 600f,
    var detectionRadius: Float = 250f,
    var attackRadius: Float = 70f,
    var moveSpeed: Float = 3.0f,
    var attackDamage: Float = 15f,
    var isAggro: Boolean = false,
    var facingRight: Boolean = true,
    var alertTimer: Float = 0f
)

enum class EnemyPresetType {
    STATIC_GUARD,
    PATROLLING_SOLDIER,
    AGGRESSIVE_MONSTER,
    PERSIAN_GULF_CORSAIR
}

enum class AIState {
    IDLE,
    PATROL,
    CHASE,
    ATTACK,
    HURT,
    STUNNED,
    VICTORY
}
