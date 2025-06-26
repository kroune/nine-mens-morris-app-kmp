package io.github.kroune.nine_mens_morris_kmp_app.navigation

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.slide
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
open class AnimateAbleConfiguration @OptIn(ExperimentalDecomposeApi::class) constructor(
    @Transient
    open var customAnimation: StackAnimator = slide()
)