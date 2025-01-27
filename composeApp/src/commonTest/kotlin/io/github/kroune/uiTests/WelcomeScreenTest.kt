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
import com.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponentI
import com.kroune.nine_mens_morris_kmp_app.event.other.WelcomeScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.other.WelcomeScreen
import io.github.kroune.all
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
import kotlin.time.Duration.Companion.seconds

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
            val passed900 =
                flowOf<Boolean>().onStart {
                    emit(false)
                    delay(900.milliseconds)
                    emit(true)
                }.stateIn(
                    CoroutineScope(Dispatchers.Default),
                    SharingStarted.Eagerly,
                    false
                )
            val passed1100 =
                flowOf<Boolean>().onStart {
                    emit(false)
                    delay(2000.milliseconds)
                    emit(true)
                }.stateIn(
                    CoroutineScope(Dispatchers.Default),
                    SharingStarted.Eagerly,
                    false
                )
            setContent {
                WelcomeScreen(component)
            }
            waitUntil(timeoutMillis = 2.seconds.inWholeMilliseconds) {
                onAllNodes(
                    hasContentDescription(
                        "game piece element with ",
                        substring = true
                    ) and hasClickAction()
                ).all {
                    it.isDisplayed()
                }
            }
            // assert that it took 900..1100 ms to finish the animation
            assertTrue(passed900.value)
            assertFalse(passed1100.value)
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