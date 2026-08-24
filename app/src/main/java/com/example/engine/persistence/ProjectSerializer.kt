package com.example.engine.persistence

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.engine.model.*
import com.example.engine.particles.ParticlePresetType
import com.example.engine.physics.ColliderDefinition
import com.example.engine.physics.ColliderShapeType
import com.example.engine.physics.CollisionLayer
import org.json.JSONArray
import org.json.JSONObject

/**
 * Serialization and Export Engine for GAME ENGINE PERSIAN GULF (.gulfproj format)
 */
object ProjectSerializer {

    fun serializeProject(
        projectId: String,
        projectName: String,
        persianName: String,
        author: String,
        version: String,
        hierarchy: List<HierarchyNode>,
        objects: List<SceneObject>,
        nodes: List<ScriptBlockNode>,
        connections: List<NodeConnection>
    ): String {
        val root = JSONObject()
        root.put("engine", EngineConstants.ENGINE_NAME)
        root.put("engineVersion", EngineConstants.VERSION)
        root.put("projectId", projectId)
        root.put("projectName", projectName)
        root.put("persianName", persianName)
        root.put("author", author)
        root.put("version", version)
        root.put("timestamp", System.currentTimeMillis())

        // Hierarchy
        val hierarchyArray = JSONArray()
        hierarchy.forEach { hierarchyArray.put(serializeHierarchyNode(it)) }
        root.put("hierarchy", hierarchyArray)

        // Scene Objects
        val objectsArray = JSONArray()
        objects.forEach { objectsArray.put(serializeSceneObject(it)) }
        root.put("sceneObjects", objectsArray)

        // Script Nodes
        val nodesArray = JSONArray()
        nodes.forEach { nodesArray.put(serializeScriptNode(it)) }
        root.put("scriptNodes", nodesArray)

        // Connections
        val connArray = JSONArray()
        connections.forEach { conn ->
            val connObj = JSONObject()
            connObj.put("id", conn.id)
            connObj.put("fromNodeId", conn.fromNodeId)
            connObj.put("fromSocketId", conn.fromSocketId)
            connObj.put("toNodeId", conn.toNodeId)
            connObj.put("toSocketId", conn.toSocketId)
            connObj.put("connectionType", conn.connectionType.name)
            connArray.put(connObj)
        }
        root.put("connections", connArray)

        return root.toString(2)
    }

    private fun serializeHierarchyNode(node: HierarchyNode): JSONObject {
        val json = JSONObject()
        json.put("id", node.id)
        json.put("name", node.name)
        json.put("persianName", node.persianName)
        json.put("type", node.type.name)
        json.put("iconType", node.iconType.name)
        json.put("isExpanded", node.isExpanded)
        json.put("isSelected", node.isSelected)
        val childrenArr = JSONArray()
        node.children.forEach { childrenArr.put(serializeHierarchyNode(it)) }
        json.put("children", childrenArr)
        return json
    }

    private fun serializeSceneObject(obj: SceneObject): JSONObject {
        val json = JSONObject()
        json.put("id", obj.id)
        json.put("name", obj.name)
        json.put("type", obj.type.name)
        json.put("x", obj.x)
        json.put("y", obj.y)
        json.put("rotation", obj.rotation)
        json.put("scaleX", obj.scaleX)
        json.put("scaleY", obj.scaleY)
        json.put("width", obj.width)
        json.put("height", obj.height)
        json.put("zIndex", obj.zIndex)
        json.put("isStatic", obj.isStatic)
        json.put("hasPhysics", obj.hasPhysics)
        json.put("mass", obj.mass)
        json.put("friction", obj.friction)
        json.put("restitution", obj.restitution)
        json.put("gravityScale", obj.gravityScale)
        json.put("spriteResName", obj.spriteResName)
        json.put("tintColor", obj.tintColor.toArgb())

        // Collider
        val colObj = JSONObject()
        colObj.put("shape", obj.collider.shape.name)
        colObj.put("offsetX", obj.collider.offsetX)
        colObj.put("offsetY", obj.collider.offsetY)
        colObj.put("width", obj.collider.width)
        colObj.put("height", obj.collider.height)
        colObj.put("radius", obj.collider.radius)
        colObj.put("isTrigger", obj.collider.isTrigger)
        colObj.put("layer", obj.collider.layer.name)
        colObj.put("mask", obj.collider.mask)
        json.put("collider", colObj)

        if (obj.particlePreset != null) {
            json.put("particlePreset", obj.particlePreset!!.name)
        }

        return json
    }

    private fun serializeScriptNode(node: ScriptBlockNode): JSONObject {
        val json = JSONObject()
        json.put("id", node.id)
        json.put("title", node.title)
        json.put("persianTitle", node.persianTitle)
        if (node.subtitle != null) json.put("subtitle", node.subtitle)
        if (node.persianSubtitle != null) json.put("persianSubtitle", node.persianSubtitle)
        json.put("category", node.category.name)
        json.put("x", node.x)
        json.put("y", node.y)

        val inSockets = JSONArray()
        node.inputSockets.forEach { s ->
            val sJson = JSONObject()
            sJson.put("id", s.id)
            sJson.put("name", s.name)
            sJson.put("type", s.type.name)
            inSockets.put(sJson)
        }
        json.put("inputSockets", inSockets)

        val outSockets = JSONArray()
        node.outputSockets.forEach { s ->
            val sJson = JSONObject()
            sJson.put("id", s.id)
            sJson.put("name", s.name)
            sJson.put("type", s.type.name)
            outSockets.put(sJson)
        }
        json.put("outputSockets", outSockets)

        return json
    }

    fun deserializeProject(jsonStr: String): DeserializedProject? {
        return try {
            val root = JSONObject(jsonStr)
            val projectId = root.optString("projectId", java.util.UUID.randomUUID().toString())
            val projectName = root.optString("projectName", "Untitled Persian Gulf Game")
            val persianName = root.optString("persianName", "بازی خلیج فارس")
            val author = root.optString("author", "Milad Aziznejad")
            val version = root.optString("version", "1.0.0")

            val hierarchyList = mutableListOf<HierarchyNode>()
            val hierArr = root.optJSONArray("hierarchy")
            if (hierArr != null) {
                for (i in 0 until hierArr.length()) {
                    hierarchyList.add(deserializeHierarchyNode(hierArr.getJSONObject(i)))
                }
            }

            val objectList = mutableListOf<SceneObject>()
            val objArr = root.optJSONArray("sceneObjects")
            if (objArr != null) {
                for (i in 0 until objArr.length()) {
                    objectList.add(deserializeSceneObject(objArr.getJSONObject(i)))
                }
            }

            val nodeList = mutableListOf<ScriptBlockNode>()
            val nodeArr = root.optJSONArray("scriptNodes")
            if (nodeArr != null) {
                for (i in 0 until nodeArr.length()) {
                    nodeList.add(deserializeScriptNode(nodeArr.getJSONObject(i)))
                }
            }

            val connList = mutableListOf<NodeConnection>()
            val connArr = root.optJSONArray("connections")
            if (connArr != null) {
                for (i in 0 until connArr.length()) {
                    val c = connArr.getJSONObject(i)
                    connList.add(
                        NodeConnection(
                            id = c.optString("id", java.util.UUID.randomUUID().toString()),
                            fromNodeId = c.getString("fromNodeId"),
                            fromSocketId = c.getString("fromSocketId"),
                            toNodeId = c.getString("toNodeId"),
                            toSocketId = c.getString("toSocketId"),
                            connectionType = SocketType.valueOf(c.optString("connectionType", "FLOW"))
                        )
                    )
                }
            }

            DeserializedProject(
                projectId = projectId,
                projectName = projectName,
                persianName = persianName,
                author = author,
                version = version,
                hierarchy = hierarchyList,
                sceneObjects = objectList,
                scriptNodes = nodeList,
                connections = connList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun deserializeHierarchyNode(json: JSONObject): HierarchyNode {
        val children = mutableListOf<HierarchyNode>()
        val chArr = json.optJSONArray("children")
        if (chArr != null) {
            for (i in 0 until chArr.length()) {
                children.add(deserializeHierarchyNode(chArr.getJSONObject(i)))
            }
        }
        return HierarchyNode(
            id = json.optString("id", java.util.UUID.randomUUID().toString()),
            name = json.getString("name"),
            persianName = json.optString("persianName", json.getString("name")),
            type = NodeType.valueOf(json.optString("type", "FOLDER")),
            iconType = IconCategory.valueOf(json.optString("iconType", "GENERIC_FOLDER")),
            isExpanded = json.optBoolean("isExpanded", true),
            isSelected = json.optBoolean("isSelected", false),
            children = children
        )
    }

    private fun deserializeSceneObject(json: JSONObject): SceneObject {
        val colJson = json.optJSONObject("collider")
        val collider = if (colJson != null) {
            ColliderDefinition(
                shape = ColliderShapeType.valueOf(colJson.optString("shape", "BOX")),
                offsetX = colJson.optDouble("offsetX", 0.0).toFloat(),
                offsetY = colJson.optDouble("offsetY", 0.0).toFloat(),
                width = colJson.optDouble("width", 100.0).toFloat(),
                height = colJson.optDouble("height", 100.0).toFloat(),
                radius = colJson.optDouble("radius", 50.0).toFloat(),
                isTrigger = colJson.optBoolean("isTrigger", false),
                layer = CollisionLayer.valueOf(colJson.optString("layer", "DEFAULT")),
                mask = colJson.optInt("mask", 0xFF)
            )
        } else ColliderDefinition()

        val particlePresetStr = json.optString("particlePreset", "")
        val particlePreset = if (particlePresetStr.isNotEmpty()) {
            try { ParticlePresetType.valueOf(particlePresetStr) } catch (_: Exception) { null }
        } else null

        val colorInt = json.optInt("tintColor", -1)
        val color = if (colorInt != -1) Color(colorInt) else Color.White

        return SceneObject(
            id = json.optString("id", java.util.UUID.randomUUID().toString()),
            name = json.getString("name"),
            type = SceneObjectType.valueOf(json.optString("type", "CUSTOM_PROP")),
            x = json.optDouble("x", 100.0).toFloat(),
            y = json.optDouble("y", 100.0).toFloat(),
            rotation = json.optDouble("rotation", 0.0).toFloat(),
            scaleX = json.optDouble("scaleX", 1.0).toFloat(),
            scaleY = json.optDouble("scaleY", 1.0).toFloat(),
            width = json.optDouble("width", 100.0).toFloat(),
            height = json.optDouble("height", 100.0).toFloat(),
            zIndex = json.optInt("zIndex", 0),
            isStatic = json.optBoolean("isStatic", false),
            hasPhysics = json.optBoolean("hasPhysics", true),
            mass = json.optDouble("mass", 1.0).toFloat(),
            friction = json.optDouble("friction", 0.5).toFloat(),
            restitution = json.optDouble("restitution", 0.2).toFloat(),
            gravityScale = json.optDouble("gravityScale", 1.0).toFloat(),
            spriteResName = json.optString("spriteResName", "sailor"),
            tintColor = color,
            collider = collider,
            particlePreset = particlePreset
        )
    }

    private fun deserializeScriptNode(json: JSONObject): ScriptBlockNode {
        val inList = mutableListOf<SocketPin>()
        val inArr = json.optJSONArray("inputSockets")
        if (inArr != null) {
            for (i in 0 until inArr.length()) {
                val s = inArr.getJSONObject(i)
                inList.add(
                    SocketPin(
                        id = s.optString("id", java.util.UUID.randomUUID().toString()),
                        name = s.getString("name"),
                        type = SocketType.valueOf(s.optString("type", "FLOW"))
                    )
                )
            }
        }

        val outList = mutableListOf<SocketPin>()
        val outArr = json.optJSONArray("outputSockets")
        if (outArr != null) {
            for (i in 0 until outArr.length()) {
                val s = outArr.getJSONObject(i)
                outList.add(
                    SocketPin(
                        id = s.optString("id", java.util.UUID.randomUUID().toString()),
                        name = s.getString("name"),
                        type = SocketType.valueOf(s.optString("type", "FLOW"))
                    )
                )
            }
        }

        return ScriptBlockNode(
            id = json.optString("id", java.util.UUID.randomUUID().toString()),
            title = json.getString("title"),
            persianTitle = json.optString("persianTitle", json.getString("title")),
            subtitle = if (json.has("subtitle")) json.getString("subtitle") else null,
            persianSubtitle = if (json.has("persianSubtitle")) json.getString("persianSubtitle") else null,
            category = NodeCategory.valueOf(json.optString("category", "ACTION")),
            x = json.optDouble("x", 100.0).toFloat(),
            y = json.optDouble("y", 100.0).toFloat(),
            inputSockets = inList,
            outputSockets = outList
        )
    }
}

data class DeserializedProject(
    val projectId: String,
    val projectName: String,
    val persianName: String,
    val author: String,
    val version: String,
    val hierarchy: List<HierarchyNode>,
    val sceneObjects: List<SceneObject>,
    val scriptNodes: List<ScriptBlockNode>,
    val connections: List<NodeConnection>
)
