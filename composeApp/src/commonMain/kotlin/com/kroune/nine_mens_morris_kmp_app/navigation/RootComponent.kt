package com.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.kroune.nine_mens_morris_kmp_app.component.AppStartAnimationComponent
import com.kroune.nine_mens_morris_kmp_app.component.WelcomeScreenComponent
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.AppStartAnimation,
        handleBackButton = true,
        childFactory = ::createChild
    )

    fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Configuration.AppStartAnimation -> {
                Child.AppStartAnimationScreenChild(
                    AppStartAnimationComponent(
                        context,
                        {
                            navigation.pushNew(Configuration.WelcomeScreen)
                        }
                    )
                )
            }
            is Configuration.WelcomeScreen -> {
                Child.WelcomeScreenChild(
                    WelcomeScreenComponent(
                        context,
                        {
//                            navigation.pushNew(Configuration.GameWithFriendScreen)
                        },
                        {
//                            navigation.pushNew(Configuration.GameWithBotScreen)
                        },
                        {
//                            navigation.pushNew(Configuration.OnlineGameScreen)
                        },
                        {
//                            navigation.pushNew(Configuration.AccountViewScreen)
                        }
                    )
                )
            }
        }
    }

    sealed class Child {
        data class AppStartAnimationScreenChild(val component: AppStartAnimationComponent): Child()
        data class WelcomeScreenChild(val component: WelcomeScreenComponent): Child()
    }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object AppStartAnimation : Configuration()
        @Serializable
        data object WelcomeScreen: Configuration()
    }
}