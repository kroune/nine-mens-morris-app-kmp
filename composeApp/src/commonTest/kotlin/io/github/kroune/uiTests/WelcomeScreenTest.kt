package io.github.kroune.uiTests

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.kroune.UiTest
import io.github.kroune.forEach
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen.WelcomeScreen
import io.github.kroune.toCollection
import kotlin.test.Test
import kotlin.test.assertTrue

class WelcomeScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @UiTest
    @Test
    fun testAnimation() {
        runComposeUiTest {
            val state = WelcomeScreenState(
                null,
                null,
                false
            )
            setContent {
                WelcomeScreen(
                    state,
                    {}
                )
            }
            mainClock.advanceTimeBy(1100)
            onAllNodes(
                hasContentDescription(
                    "game piece element with ",
                    substring = true
                ) and hasClickAction()
            ).toCollection().filter { it.isDisplayed() }.let {
                assertTrue { it.size == 24 }
            }
            // assert that it took less than 1100 ms to finish the animation
            bottomBarCheck()
            onAllNodes(
                hasContentDescription(
                    "game piece element with ",
                    substring = true
                ) and hasClickAction()
            ).forEach {
                it.assertExists()
                it.assertIsDisplayed()
                it.assertHasClickAction()
                it.assertHeightIsAtLeast(20.dp)
                it.assertWidthIsAtLeast(20.dp)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    fun ComposeUiTest.bottomBarCheck() {
        val goToSettings = onNodeWithContentDescription("go to settings button")
        goToSettings.assertExists()
        goToSettings.assertIsDisplayed()
        goToSettings.assertHasClickAction()
        goToSettings.assertWidthIsAtLeast(50.dp)
        goToSettings.assertWidthIsAtLeast(50.dp)
        val scrollUpOrDown = onNodeWithContentDescription("scroll up or down")
        scrollUpOrDown.assertExists()
        scrollUpOrDown.assertIsDisplayed()
        scrollUpOrDown.assertHasClickAction()
        scrollUpOrDown.assertWidthIsAtLeast(50.dp)
        scrollUpOrDown.assertWidthIsAtLeast(50.dp)
        val accountInformation =
            onNodeWithContentDescription("account information", substring = true)
        accountInformation.assertExists()
        accountInformation.assertIsDisplayed()
        accountInformation.assertWidthIsAtLeast(50.dp)
        accountInformation.assertWidthIsAtLeast(50.dp)
    }
}