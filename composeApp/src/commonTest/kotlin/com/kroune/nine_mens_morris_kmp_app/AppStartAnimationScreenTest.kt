package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponentI
import com.kroune.nine_mens_morris_kmp_app.event.other.AppStartAnimationScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppStartAnimationScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        try {
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
                onNodeWithTag("pressToStart", true).assertTextEquals("Press to start")
                onNodeWithTag("pressToStart", true).assertIsDisplayed()
                onNodeWithTag("pressToStart", true).assertHeightIsAtLeast(10.dp)
                onNodeWithTag("pressToStart", true).assertWidthIsAtLeast(30.dp)
                assertFalse(actionPerformed)
                onNodeWithTag("pressToStart", true).performClick()
                assertTrue(actionPerformed)
            }
        } catch (e: NullPointerException) {
            // doesn't work on android local
            if (e.message != "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"android.os.Build.FINGERPRINT\" is null") {
                throw e
            }
        }
    }
}