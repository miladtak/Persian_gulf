package com.example.engine.animation

import java.util.UUID

enum class AnimationState {
    IDLE,
    WALK,
    RUN,
    JUMP,
    ATTACK,
    HURT,
    DIE
}

data class SpriteFrame(
    val index: Int,
    val durationMs: Long = 100L,
    val uMin: Float = 0f,
    val vMin: Float = 0f,
    val uMax: Float = 1f,
    val vMax: Float = 1f
)

data class AnimationClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String,
    val state: AnimationState = AnimationState.IDLE,
    val frameCount: Int = 4,
    val fps: Int = 8,
    val isLooping: Boolean = true,
    val isPingPong: Boolean = false,
    val frames: List<SpriteFrame> = (0 until frameCount).map { i ->
        SpriteFrame(index = i, durationMs = (1000L / fps))
    }
)

data class SpriteAnimatorComponent(
    var currentClipName: String = "Idle",
    var isPlaying: Boolean = true,
    var currentFrameIndex: Int = 0,
    var timeAccumulatorMs: Long = 0L,
    val availableClips: MutableMap<String, AnimationClip> = mutableMapOf()
) {
    fun update(dtMs: Long) {
        if (!isPlaying) return
        val clip = availableClips[currentClipName] ?: return
        if (clip.frames.isEmpty()) return

        timeAccumulatorMs += dtMs
        val frameDuration = 1000L / clip.fps.coerceAtLeast(1)

        if (timeAccumulatorMs >= frameDuration) {
            timeAccumulatorMs %= frameDuration
            currentFrameIndex++
            if (currentFrameIndex >= clip.frameCount) {
                if (clip.isLooping) {
                    currentFrameIndex = 0
                } else {
                    currentFrameIndex = clip.frameCount - 1
                    isPlaying = false
                }
            }
        }
    }

    fun play(clipName: String, loop: Boolean = true) {
        if (currentClipName == clipName && isPlaying) return
        currentClipName = clipName
        currentFrameIndex = 0
        timeAccumulatorMs = 0L
        isPlaying = true
    }
}
