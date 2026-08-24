package com.example.engine.model

import java.util.UUID

/**
 * Engine Plugin & Extension API for Milad Aziznejad
 */
interface EnginePlugin {
    val pluginId: String
    val name: String
    val persianName: String
    val author: String
    val version: String
    val description: String
    val persianDescription: String
    val isEnabled: Boolean
    
    fun onEngineInit()
    fun getCustomNodes(): List<ScriptBlockNode> = emptyList()
    fun getCustomAssets(): List<String> = emptyList()
}

data class RegisteredPlugin(
    val pluginId: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String,
    val author: String = "Milad Aziznejad",
    val version: String = "1.0.0",
    val description: String,
    val persianDescription: String,
    var isEnabled: Boolean = true,
    val category: String = "Behavior",
    val codeSnippet: String = ""
)
