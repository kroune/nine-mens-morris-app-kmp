package io.github.kroune.uiTests

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilNodeCount
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponentI
import com.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.other.WelcomeScreen
import io.github.kroune.forEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class WelcomeScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testAnimation() {
        runComposeUiTest {
            val component = object : WelcomeScreenComponentI {
                override val isInAccount: StateFlow<Result<Boolean>?> =
                    flowOf<Result<Boolean>?>().onStart {
                        emit(null)
                    }.stateIn(
                        CoroutineScope(Dispatchers.Default),
                        SharingStarted.WhileSubscribed(),
                        null
                    )

                override fun onEvent(event: WelcomeScreenEvent) {
                    TODO("Not yet implemented")
                }

                override val accountIdFailure: Throwable? = null
                override val hasSeenTutorial: Boolean = false
            }
            val time600 =
                flowOf<Boolean>().onStart {
                    emit(true)
                    delay(600.milliseconds)
                    emit(false)
                }.stateIn(
                    CoroutineScope(Dispatchers.Default),
                    SharingStarted.Eagerly,
                    true
                )
            val time1000 =
                flowOf<Boolean>().onStart {
                    emit(true)
                    delay(1000.milliseconds)
                    emit(false)
                }.stateIn(
                    CoroutineScope(Dispatchers.Default),
                    SharingStarted.Eagerly,
                    true
                )
            setContent {
                WelcomeScreen(component)
            }
            // wait until scroll animation finishes
            waitUntilNodeCount(
                hasContentDescription(
                    "game piece element with ",
                    substring = true
                ) and hasClickAction(),
                24
            )
            // assert that it took 600..800 ms to finish the animation
            assertFalse(time600.value)
            waitForIdle()
            assertTrue(time1000.value)
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
                it.assertHeightIsAtLeast(30.dp)
                it.assertWidthIsAtLeast(30.dp)
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