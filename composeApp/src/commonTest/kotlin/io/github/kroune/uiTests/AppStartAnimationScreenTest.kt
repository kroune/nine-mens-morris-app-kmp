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
import com.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponentI
import com.kroune.nine_mens_morris_kmp_app.event.other.AppStartAnimationScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import io.github.kroune.UiTest
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
            val component = object : AppStartAnimationComponentI {
                override fun onEvent(event: AppStartAnimationScreenEvent) {
                    when (event) {
                        AppStartAnimationScreenEvent.ClickButton -> {
                            actionPerformed = true
                        }
                    }
                }
            }
            setContent {
                AppStartAnimationScreen(component)
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