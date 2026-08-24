package com.example.engine.persistence

import android.content.Context
import android.content.SharedPreferences

/**
 * Runtime Player Data persistence system for GAME ENGINE PERSIAN GULF
 * Used by in-game scripts (SaveData, LoadData, HasKey, DeleteKey)
 * to save player progress, checkpoints, high scores, and inventory state.
 */
class RuntimeSaveSystem(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("gulf_game_save_data", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun loadString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    fun saveNumber(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun loadNumber(key: String, defaultValue: Float = 0f): Float {
        return prefs.getFloat(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun loadBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun hasKey(key: String): Boolean {
        return prefs.contains(key)
    }

    fun deleteKey(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
