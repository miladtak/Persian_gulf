package com.example.engine.persistence

import com.example.engine.model.HierarchyNode
import com.example.engine.model.NodeConnection
import com.example.engine.model.SceneObject
import com.example.engine.model.ScriptBlockNode
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val templates: Flow<List<ProjectEntity>> = projectDao.getTemplates()

    suspend fun getProjectById(projectId: String): ProjectEntity? {
        return projectDao.getProjectById(projectId)
    }

    suspend fun saveProject(
        projectId: String,
        title: String,
        persianTitle: String,
        description: String,
        author: String,
        version: String,
        hierarchy: List<HierarchyNode>,
        objects: List<SceneObject>,
        nodes: List<ScriptBlockNode>,
        connections: List<NodeConnection>,
        isTemplate: Boolean = false
    ): Long {
        val serialized = ProjectSerializer.serializeProject(
            projectId = projectId,
            projectName = title,
            persianName = persianTitle,
            author = author,
            version = version,
            hierarchy = hierarchy,
            objects = objects,
            nodes = nodes,
            connections = connections
        )

        val entity = ProjectEntity(
            projectId = projectId,
            title = title,
            persianTitle = persianTitle,
            description = description,
            author = author,
            version = version,
            updatedAt = System.currentTimeMillis(),
            sceneObjectsJson = serialized,
            scriptNodesJson = "",
            scriptConnectionsJson = "",
            hierarchyJson = "",
            isTemplate = isTemplate
        )

        return projectDao.insertOrUpdateProject(entity)
    }

    suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
    }
}
