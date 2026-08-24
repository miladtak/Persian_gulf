package com.example.engine.particles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class ParticlePresetType {
    PERSIAN_GOLD_DUST,
    GULF_WATER_SPLASH,
    MAGIC_SPARKS,
    FIRE_TORCH,
    EXPLOSION_BURST,
    RAIN_DROPS,
    DESERT_SANDSTORM,
    SWORD_SLASH_SPARKS
}

data class Particle(
    val id: Long = System.nanoTime(),
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    var maxLife: Float,
    var size: Float,
    var startSize: Float,
    var endSize: Float,
    var color: Color,
    var startColor: Color,
    var endColor: Color,
    var alpha: Float = 1f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f
)

data class ParticleEmitterConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val preset: ParticlePresetType = ParticlePresetType.PERSIAN_GOLD_DUST,
    var x: Float = 0f,
    var y: Float = 0f,
    var isEmitting: Boolean = true,
    var ratePerSecond: Int = 30,
    var particleLifetime: Float = 1.2f,
    var minSpeed: Float = 30f,
    var maxSpeed: Float = 120f,
    var spreadAngleDegrees: Float = 360f,
    var baseAngleDegrees: Float = 270f, // Upward
    var gravityY: Float = 40f,
    var startColor: Color = Color(0xFFFFD700),
    var endColor: Color = Color(0xFFFF9100),
    var startSize: Float = 8f,
    var endSize: Float = 2f
)
