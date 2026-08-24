package com.example.engine.model

import java.util.UUID

/**
 * Visual Scripting Blocks (Right Panel)
 */
data class ScriptBlockNode(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val persianTitle: String,
    val subtitle: String? = null,
    val persianSubtitle: String? = null,
    val category: NodeCategory,
    var x: Float,
    var y: Float,
    val inputSockets: List<SocketPin> = emptyList(),
    val outputSockets: List<SocketPin> = emptyList(),
    var properties: Map<String, String> = emptyMap(),
    val isHighlighted: Boolean = false
)

enum class NodeCategory {
    EVENT_TRIGGER,
    ACTION,
    VARIABLE,
    LOGIC,
    ITEM_SYSTEM,
    AI_CONTROL,
    MULTIPLAYER,
    WEATHER,
    AUDIO_ENGINE,
    PHYSICS_COLLISION,
    ANIMATION_SYSTEM,
    PARTICLE_FX,
    SAVE_LOAD_DATA
}

data class SocketPin(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String = "",
    val type: SocketType = SocketType.FLOW,
    val isConnected: Boolean = false
)

enum class SocketType {
    FLOW, // Execution wire
    DATA_NUMBER,
    DATA_STRING,
    DATA_OBJECT,
    DATA_BOOLEAN
}

data class NodeConnection(
    val id: String = UUID.randomUUID().toString(),
    val fromNodeId: String,
    val fromSocketId: String,
    val toNodeId: String,
    val toSocketId: String,
    val connectionType: SocketType = SocketType.FLOW
)
