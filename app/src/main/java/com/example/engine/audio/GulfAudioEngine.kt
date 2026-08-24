package com.example.engine.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Audio Synthesizer Engine for GAME ENGINE PERSIAN GULF
 * Generates retro 8-bit / 16-bit SFX and soothing Gulf wave ambient soundtracks
 * in real-time using raw PCM streaming with AudioTrack.
 */
enum class SfxSoundType {
    JUMP,
    COIN_COLLECT,
    SWORD_ATTACK,
    EXPLOSION,
    HIT_DAMAGE,
    POWERUP_FANFARE,
    LASER_FIRE,
    DODGE_DASH,
    BUTTON_CLICK,
    OCEAN_SPLASH,
    VICTORY_CHIME
}

class GulfAudioEngine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())) {

    private val sampleRate = 22050
    var masterVolume: Float = 0.8f
    var sfxVolume: Float = 0.9f
    var bgmVolume: Float = 0.5f
    var isMuted: Boolean = false

    private var bgmJob: Job? = null
    private var isBgmPlaying: Boolean = false

    /**
     * Synthesizes and plays a procedural Sound Effect without needing external audio files.
     */
    fun playSfx(type: SfxSoundType, pitchMultiplier: Float = 1.0f) {
        if (isMuted || sfxVolume <= 0.01f || masterVolume <= 0.01f) return

        scope.launch(Dispatchers.Default) {
            try {
                val effectiveVol = masterVolume * sfxVolume
                val pcmData = when (type) {
                    SfxSoundType.JUMP -> generateJumpSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.COIN_COLLECT -> generateCoinSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.SWORD_ATTACK -> generateSlashSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.EXPLOSION -> generateExplosionSound(effectiveVol)
                    SfxSoundType.HIT_DAMAGE -> generateHitSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.POWERUP_FANFARE -> generatePowerupSound(effectiveVol)
                    SfxSoundType.LASER_FIRE -> generateLaserSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.DODGE_DASH -> generateDashSound(effectiveVol)
                    SfxSoundType.BUTTON_CLICK -> generateClickSound(pitchMultiplier, effectiveVol)
                    SfxSoundType.OCEAN_SPLASH -> generateSplashSound(effectiveVol)
                    SfxSoundType.VICTORY_CHIME -> generateVictorySound(effectiveVol)
                }
                playPcmBuffer(pcmData)
            } catch (e: Exception) {
                Log.w("GulfAudioEngine", "Error synthesizing SFX: ${e.message}")
            }
        }
    }

    /**
     * Starts continuous ambient procedural Persian Gulf soundtrack
     */
    fun startBgm() {
        if (isBgmPlaying) return
        isBgmPlaying = true

        bgmJob = scope.launch(Dispatchers.Default) {
            var phase = 0.0
            val bufferSize = sampleRate / 2 // 0.5 sec chunks
            val buffer = ShortArray(bufferSize)

            while (isActive && isBgmPlaying) {
                if (isMuted || bgmVolume <= 0.01f || masterVolume <= 0.01f) {
                    delay(200)
                    continue
                }

                val vol = masterVolume * bgmVolume
                for (i in 0 until bufferSize) {
                    val t = phase / sampleRate
                    // Harmonic chord progression (Persian Bayati / Gulf Modal Ambience: D minor / G minor)
                    val freqBass = 146.83 // D3
                    val freqMid = 220.00  // A3
                    val freqMelody = 293.66 // D4

                    // Subtle ocean wave pink-noise modulation
                    val waveLfo = (sin(2.0 * PI * 0.15 * t) + 1.0) * 0.5
                    val pinkNoise = (Random.nextFloat() * 2f - 1f) * 0.15f * waveLfo.toFloat()

                    // Gentle synth sine waves
                    val bassWave = sin(2.0 * PI * freqBass * t) * 0.25
                    val midWave = sin(2.0 * PI * freqMid * t) * 0.15
                    val melodyWave = sin(2.0 * PI * freqMelody * t + sin(2.0 * PI * 2.0 * t)) * 0.1

                    val combined = (bassWave + midWave + melodyWave + pinkNoise) * vol * Short.MAX_VALUE
                    buffer[i] = combined.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    phase++
                }

                playPcmBuffer(buffer)
            }
        }
    }

    fun startGulfAmbienceBgm() {
        startBgm()
    }

    fun stopBgm() {
        isBgmPlaying = false
        bgmJob?.cancel()
        bgmJob = null
    }

    fun release() {
        stopBgm()
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

        val durationMs = (buffer.size.toFloat() / sampleRate * 1000).toLong()
        scope.launch(Dispatchers.Default) {
            delay(durationMs + 50)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    // --- Sound Synthesis Algorithms ---

    private fun generateJumpSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.16f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        var freq = 160.0 * pitch
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            freq += 3.5 * pitch
            val sample = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val envelope = (1.0 - progress) * vol * Short.MAX_VALUE
            buffer[i] = (sample * envelope).toInt().toShort()
        }
        return buffer
    }

    private fun generateCoinSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.22f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        val half = duration / 2
        for (i in 0 until duration) {
            val freq = if (i < half) 987.77 * pitch else 1318.51 * pitch
            val sample = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val env = (1.0 - (i.toDouble() / duration)) * vol * Short.MAX_VALUE
            buffer[i] = (sample * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateSlashSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.18f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val noise = Random.nextFloat() * 2f - 1f
            val tone = sin(2.0 * PI * (400.0 * (1.0 - progress * 0.7) * pitch) * (i.toDouble() / sampleRate))
            val envelope = sin(progress * PI) * vol * Short.MAX_VALUE
            buffer[i] = ((noise * 0.7 + tone * 0.3) * envelope).toInt().toShort()
        }
        return buffer
    }

    private fun generateExplosionSound(vol: Float): ShortArray {
        val duration = (0.35f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val noise = Random.nextFloat() * 2f - 1f
            val envelope = Math.exp(-progress * 5.0) * vol * Short.MAX_VALUE
            buffer[i] = (noise * envelope).toInt().toShort()
        }
        return buffer
    }

    private fun generateHitSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.14f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val freq = 120.0 * (1.0 - progress * 0.6) * pitch
            val square = if (sin(2.0 * PI * freq * (i.toDouble() / sampleRate)) > 0) 1.0 else -1.0
            val noise = Random.nextFloat() * 2f - 1f
            val env = (1.0 - progress) * vol * Short.MAX_VALUE
            buffer[i] = ((square * 0.6 + noise * 0.4) * env).toInt().toShort()
        }
        return buffer
    }

    private fun generatePowerupSound(vol: Float): ShortArray {
        val duration = (0.32f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        val freqs = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25)
        val step = duration / freqs.size
        for (i in 0 until duration) {
            val noteIdx = (i / step).coerceIn(0, freqs.size - 1)
            val freq = freqs[noteIdx]
            val sample = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val env = (1.0 - (i % step).toDouble() / step * 0.3) * vol * Short.MAX_VALUE
            buffer[i] = (sample * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateLaserSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.15f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val freq = (1200.0 * (1.0 - progress * 0.85)) * pitch
            val sample = sin(2.0 * PI * freq * (i.toDouble() / sampleRate))
            val env = (1.0 - progress) * vol * Short.MAX_VALUE
            buffer[i] = (sample * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateDashSound(vol: Float): ShortArray {
        val duration = (0.12f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val noise = Random.nextFloat() * 2f - 1f
            val env = sin(progress * PI) * vol * Short.MAX_VALUE * 0.8
            buffer[i] = (noise * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateClickSound(pitch: Float, vol: Float): ShortArray {
        val duration = (0.04f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val sample = sin(2.0 * PI * 880.0 * pitch * (i.toDouble() / sampleRate))
            val env = (1.0 - progress) * vol * Short.MAX_VALUE * 0.5
            buffer[i] = (sample * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateSplashSound(vol: Float): ShortArray {
        val duration = (0.28f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            val noise = Random.nextFloat() * 2f - 1f
            val tone = sin(2.0 * PI * 180.0 * (1.0 + progress * 0.5) * (i.toDouble() / sampleRate))
            val env = (1.0 - progress) * vol * Short.MAX_VALUE
            buffer[i] = ((noise * 0.8 + tone * 0.2) * env).toInt().toShort()
        }
        return buffer
    }

    private fun generateVictorySound(vol: Float): ShortArray {
        val duration = (0.45f * sampleRate).toInt()
        val buffer = ShortArray(duration)
        val freqs = doubleArrayOf(440.0, 554.37, 659.25, 880.0)
        val step = duration / freqs.size
        for (i in 0 until duration) {
            val noteIdx = (i / step).coerceIn(0, freqs.size - 1)
            val sample = sin(2.0 * PI * freqs[noteIdx] * (i.toDouble() / sampleRate))
            val env = (1.0 - (i.toDouble() / duration) * 0.5) * vol * Short.MAX_VALUE
            buffer[i] = (sample * env).toInt().toShort()
        }
        return buffer
    }
}
