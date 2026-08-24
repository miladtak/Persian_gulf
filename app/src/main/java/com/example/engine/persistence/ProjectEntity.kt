package com.example.engine.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String = UUID.randomUUID().toString(),
    val title: String,
    val persianTitle: String,
    val description: String,
    val author: String = "Milad Aziznejad",
    val version: String = "1.0.0",
    val targetPlatform: String = "Android 2D",
    val updatedAt: Long = System.currentTimeMillis(),
    val sceneObjectsJson: String,
    val scriptNodesJson: String,
    val scriptConnectionsJson: String,
    val hierarchyJson: String,
    val isTemplate: Boolean = false,
    val iconKey: String = "dhow"
)
