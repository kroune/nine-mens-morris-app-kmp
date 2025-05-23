package io.github.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.LeaderboardScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.appStartAnimationComponent.AppStartAnimationComponent
import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


sealed class Child(open val component: ComponentContextWithBackHandle) {
    data class AppStartAnimationScreenChild(
        override val component: AppStartAnimationComponent
    ) : Child(component)

    data class WelcomeScreenChild(
        override val component: WelcomeScreenComponent
    ) : Child(component)

    data class ViewOwnAccountScreenChild(
        override val component: ViewOwnAccountScreenComponent
    ) : Child(component)

    data class ViewAccountScreenChild(
        override val component: ViewAccountScreenComponent
    ) : Child(component)

    data class SignUpScreenChild(
        override val component: SignUpScreenComponent
    ) : Child(component)

    data class SignInScreenChild(
        override val component: SignInScreenComponent
    ) : Child(component)

    data class GameWithFriendChild(
        override val component: GameWithFriendScreenComponent
    ) : Child(component)

    data class GameWithBotChild(
        override val component: GameWithBotScreenComponent
    ) : Child(component)

    data class SearchingForGameChild(
        override val component: SearchingForGameComponent
    ) : Child(component)

    data class OnlineGameChild(
        override val component: OnlineGameComponent
    ) : Child(component)

    data class LeaderboardChild(
        override val component: LeaderboardScreenComponent
    ) : Child(component)
}

@Serializable
sealed class Configuration(
    val urlName: String,
    @Transient
    var animation: StackAnimator = slide()
) {
    @Serializable
    data class AppStartAnimation(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("", scale() + fade())

    @Serializable
    data class WelcomeScreen(
        @Transient
        val customAnimation: StackAnimator = scale() + fade()
    ) : Configuration("welcome", customAnimation)

    @Serializable
    data class ViewOwnAccountScreen(
        val accountId: Long,
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("account-$accountId", customAnimation)

    @Serializable
    data class ViewAccountScreen(
        val accountId: Long,
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("account-$accountId", customAnimation)

    /**
     * We don't pass lambda for navigation to the next destination
     * because it can't be serialized, so we simply pop the screen in the end
     */
    @Serializable
    data class SignUpScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("signup", customAnimation)

    /**
     * We don't pass lambda for navigation to the next destination
     * because it can't be serialized, so we simply pop the screen in the end
     */
    @Serializable
    data class SignInScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("signin", customAnimation)

    @Serializable
    data class GameWithFriendScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("game-with-friend", customAnimation)

    @Serializable
    data class GameWithBotScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("game-with-bot", customAnimation)

    @Serializable
    data class SearchingForGameScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("searching-for-game", customAnimation)

    @Serializable
    data class OnlineGameScreen(
        val gameId: Long,
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("online-game", customAnimation)

    @Serializable
    data class LeaderboardScreen(
        @Transient
        val customAnimation: StackAnimator = slide()
    ) : Configuration("leaderboard", customAnimation)
}