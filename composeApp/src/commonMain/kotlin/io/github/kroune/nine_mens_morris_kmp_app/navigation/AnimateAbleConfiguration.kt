package io.github.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
open class AnimateAbleConfiguration(
    @Transient
    open var customAnimation: StackAnimator = slide()
)