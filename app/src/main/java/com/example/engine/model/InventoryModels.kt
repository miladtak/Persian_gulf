package com.example.engine.model

import java.util.UUID

/**
 * Inventory & Item System Data Models
 */
data class GameItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val persianName: String,
    val description: String,
    val persianDescription: String,
    val iconKey: String,
    var count: Int = 1,
    val maxStack: Int = 99,
    val isStackable: Boolean = true,
    val itemType: ItemType = ItemType.QUEST,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val powerBoost: Int = 0,
    val healthRestore: Int = 0
)

enum class ItemType {
    WEAPON,
    CONSUMABLE,
    QUEST,
    TREASURE,
    TOOL
}

enum class ItemRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY_GULF
}

data class InventoryBag(
    val capacity: Int = 24,
    val items: MutableList<GameItem> = mutableListOf(),
    var goldCoins: Int = 350
)
