package io.github.kroune.uiTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.kroune.UiTest
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppStartAnimationScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @UiTest
    @Test
    fun test() {
        runComposeUiTest {
            var actionPerformed = false
            setContent {
                AppStartAnimationScreen(
                    onEvent = {
                        actionPerformed = true
                    }
                )
            }
            val pressToStartNode = onNodeWithText("Press to start", useUnmergedTree = true)
            pressToStartNode.assertTextEquals("Press to start")
            pressToStartNode.assertIsDisplayed()
            pressToStartNode.assertHeightIsAtLeast(10.dp)
            pressToStartNode.assertWidthIsAtLeast(30.dp)
            assertFalse(actionPerformed)
            pressToStartNode.performClick()
            assertTrue(actionPerformed)
        }
    }
}