package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.model.EngineConstants
import com.example.engine.model.SceneObjectType
import com.example.engine.state.GameEngineViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Game Engine Persian Gulf", appName)
  }

  @Test
  fun `verify commercial license and engine owner constants`() {
    assertEquals("Milad Aziznejad", EngineConstants.OWNER_NAME)
    assertEquals("55555milad3603", EngineConstants.MASTER_LICENSE_KEY)
    assertEquals("GAME ENGINE PERSIAN GULF", EngineConstants.ENGINE_NAME)
  }

  @Test
  fun `verify engine viewModel initialization`() {
    val viewModel = GameEngineViewModel()
    val state = viewModel.uiState.value

    // Verify Hierarchy structure
    assertTrue(state.hierarchyTree.any { it.name == "Scenes" })
    assertTrue(state.hierarchyTree.any { it.name == "Scripts" })
    assertTrue(state.hierarchyTree.any { it.name == "Textures" })
    assertTrue(state.hierarchyTree.any { it.name == "Sound" })
    assertTrue(state.hierarchyTree.any { it.name == "Fonts" })
    assertTrue(state.hierarchyTree.any { it.name == "Settings" })
    assertTrue(state.hierarchyTree.any { it.name == "Plugins" })
    assertTrue(state.hierarchyTree.any { it.name == "Items" })

    // Verify Visual Scripting Nodes
    assertTrue(state.scriptNodes.any { it.title == "On_Start" })
    assertTrue(state.scriptNodes.any { it.title == "Get_Item" })
    assertTrue(state.scriptNodes.any { it.title == "Use_Item" })
    assertTrue(state.scriptNodes.any { it.title == "Attack" })

    // Verify Scene Objects
    assertTrue(state.sceneObjects.any { it.type == SceneObjectType.PLAYER })
    assertTrue(state.sceneObjects.any { it.type == SceneObjectType.SHIP_DHOW })

    // Test Play Mode toggle
    viewModel.togglePlayMode()
    assertTrue(viewModel.uiState.value.isPlayMode)
  }
}
