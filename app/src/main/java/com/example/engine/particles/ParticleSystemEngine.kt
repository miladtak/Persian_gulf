package com.example.engine.particles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Real-time 2D Particle Engine for GAME ENGINE PERSIAN GULF
 * Handles multi-emitter simulations, bursts, gravity, lifetime interpolation,
 * and high-performance frame updates.
 */
class ParticleSystemEngine {

    private val particles = mutableListOf<Particle>()
    val emitters = mutableListOf<ParticleEmitterConfig>()

    init {
        // Default ambient Persian Gold Dust and Gulf Waves emitters
        emitters.add(
            createPresetEmitter(
                name = "Ambient Gulf Gold",
                preset = ParticlePresetType.PERSIAN_GOLD_DUST,
                x = 400f,
                y = 200f
            )
        )
    }

    fun update(dt: Float) {
        // 1. Emit new particles from active emitters
        for (emitter in emitters) {
            if (!emitter.isEmitting) continue
            val particlesToSpawn = (emitter.ratePerSecond * dt).toInt().coerceAtLeast(1)
            for (i in 0 until particlesToSpawn) {
                particles.add(spawnParticle(emitter))
            }
        }

        // 2. Update active particles
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.life -= dt
            if (p.life <= 0f) {
                iterator.remove()
                continue
            }

            val progress = 1f - (p.life / p.maxLife)
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.vy += 60f * dt // ambient gravity drift

            p.size = p.startSize + (p.endSize - p.startSize) * progress
            p.alpha = (1f - progress).coerceIn(0f, 1f)
            p.color = lerp(p.startColor, p.endColor, progress)
            p.rotation += p.rotationSpeed * dt
        }

        // Limit max active particles for optimal frame rate
        if (particles.size > 600) {
            val excess = particles.size - 600
            for (i in 0 until excess) {
                particles.removeAt(0)
            }
        }
    }

    fun getActiveParticles(): List<Particle> = particles.toList()

    fun burstPreset(preset: ParticlePresetType, x: Float, y: Float, count: Int = 20) {
        burst(preset, x, y, count)
    }

    fun burst(preset: ParticlePresetType, x: Float, y: Float, count: Int = 20) {
        val tempEmitter = createPresetEmitter("Burst", preset, x, y)
        for (i in 0 until count) {
            particles.add(spawnParticle(tempEmitter))
        }
    }

    private fun spawnParticle(emitter: ParticleEmitterConfig): Particle {
        val angleSpread = Math.toRadians(emitter.spreadAngleDegrees.toDouble())
        val baseAngle = Math.toRadians(emitter.baseAngleDegrees.toDouble())
        val randomAngle = baseAngle + (Random.nextDouble() - 0.5) * angleSpread
        val speed = emitter.minSpeed + Random.nextFloat() * (emitter.maxSpeed - emitter.minSpeed)

        val vx = (cos(randomAngle) * speed).toFloat()
        val vy = (sin(randomAngle) * speed).toFloat()

        return Particle(
            x = emitter.x + (Random.nextFloat() * 20f - 10f),
            y = emitter.y + (Random.nextFloat() * 20f - 10f),
            vx = vx,
            vy = vy,
            life = emitter.particleLifetime * (0.8f + Random.nextFloat() * 0.4f),
            maxLife = emitter.particleLifetime,
            size = emitter.startSize,
            startSize = emitter.startSize,
            endSize = emitter.endSize,
            color = emitter.startColor,
            startColor = emitter.startColor,
            endColor = emitter.endColor,
            alpha = 1f,
            rotation = Random.nextFloat() * 360f,
            rotationSpeed = (Random.nextFloat() * 180f - 90f)
        )
    }

    fun createPresetEmitter(name: String, preset: ParticlePresetType, x: Float, y: Float): ParticleEmitterConfig {
        return when (preset) {
            ParticlePresetType.PERSIAN_GOLD_DUST -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 18,
                particleLifetime = 1.6f,
                minSpeed = 15f,
                maxSpeed = 60f,
                spreadAngleDegrees = 360f,
                gravityY = -10f,
                startColor = Color(0xFFFFD700),
                endColor = Color(0xFFFF9100),
                startSize = 6f,
                endSize = 1.5f
            )
            ParticlePresetType.GULF_WATER_SPLASH -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 25,
                particleLifetime = 0.8f,
                minSpeed = 60f,
                maxSpeed = 180f,
                spreadAngleDegrees = 110f,
                baseAngleDegrees = 270f,
                gravityY = 180f,
                startColor = Color(0xFF64FFDA),
                endColor = Color(0xFF00B0FF),
                startSize = 7f,
                endSize = 2f
            )
            ParticlePresetType.MAGIC_SPARKS -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 30,
                particleLifetime = 1.0f,
                minSpeed = 40f,
                maxSpeed = 140f,
                spreadAngleDegrees = 360f,
                gravityY = 0f,
                startColor = Color(0xFFE040FB),
                endColor = Color(0xFF7C4DFF),
                startSize = 8f,
                endSize = 2f
            )
            ParticlePresetType.FIRE_TORCH -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 35,
                particleLifetime = 0.7f,
                minSpeed = 30f,
                maxSpeed = 100f,
                spreadAngleDegrees = 60f,
                baseAngleDegrees = 270f,
                gravityY = -80f,
                startColor = Color(0xFFFF5252),
                endColor = Color(0xFFFFD700),
                startSize = 10f,
                endSize = 2f
            )
            ParticlePresetType.EXPLOSION_BURST -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 0, // Used for bursts
                particleLifetime = 0.9f,
                minSpeed = 80f,
                maxSpeed = 240f,
                spreadAngleDegrees = 360f,
                gravityY = 80f,
                startColor = Color(0xFFFF6E40),
                endColor = Color(0xFF37474F),
                startSize = 14f,
                endSize = 1f
            )
            ParticlePresetType.RAIN_DROPS -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 40,
                particleLifetime = 1.2f,
                minSpeed = 180f,
                maxSpeed = 320f,
                spreadAngleDegrees = 15f,
                baseAngleDegrees = 85f,
                gravityY = 120f,
                startColor = Color(0xFF80D8FF),
                endColor = Color(0xFF0091EA),
                startSize = 4f,
                endSize = 2f
            )
            ParticlePresetType.DESERT_SANDSTORM -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 30,
                particleLifetime = 1.8f,
                minSpeed = 100f,
                maxSpeed = 250f,
                spreadAngleDegrees = 25f,
                baseAngleDegrees = 0f, // horizontal sweep
                gravityY = 10f,
                startColor = Color(0xFFFFE082),
                endColor = Color(0xFFBCAAA4),
                startSize = 5f,
                endSize = 2f
            )
            ParticlePresetType.SWORD_SLASH_SPARKS -> ParticleEmitterConfig(
                name = name,
                preset = preset,
                x = x,
                y = y,
                ratePerSecond = 0,
                particleLifetime = 0.4f,
                minSpeed = 90f,
                maxSpeed = 260f,
                spreadAngleDegrees = 90f,
                baseAngleDegrees = 315f,
                gravityY = 40f,
                startColor = Color(0xFFFFFF00),
                endColor = Color(0xFFFF3D00),
                startSize = 9f,
                endSize = 2f
            )
        }
    }
}
