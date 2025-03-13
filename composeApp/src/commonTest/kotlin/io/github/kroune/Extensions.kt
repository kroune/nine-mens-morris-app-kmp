package io.github.kroune

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection

inline fun SemanticsNodeInteractionCollection.size(): Int {
    for (i in 0..Int.MAX_VALUE) {
        try {
            // this is hacky, but I don't think there is any other way
            this[i].assertExists()
        } catch (e: AssertionError) {
            return i
        }
    }
    error("either there is ${Int.MAX_VALUE} nodes or smth is wrong")
}

inline fun <T> SemanticsNodeInteractionCollection.map(lambda: (SemanticsNodeInteraction) -> T): MutableList<T> {
    val result = mutableListOf<T>()
    forEach {
        result.add(
            lambda(it)
        )
    }
    return result
}

inline fun SemanticsNodeInteractionCollection.forEach(lambda: (SemanticsNodeInteraction) -> Unit) {
    for (i in 0 until size()) {
        val element = this[i]
        lambda(element)
    }
}

inline fun SemanticsNodeInteractionCollection.all(lambda: (SemanticsNodeInteraction) -> Boolean): Boolean {
    var allMatch = true
    forEach {
        if (!lambda(it))
            allMatch = false
    }
    return allMatch
}

inline fun SemanticsNodeInteractionCollection.toCollection(): MutableList<SemanticsNodeInteraction> {
    val destination = mutableListOf<SemanticsNodeInteraction>()
    forEach { element ->
        destination.add(element)
    }
    return destination
}

inline fun SemanticsNodeInteractionCollection.none(lambda: (SemanticsNodeInteraction) -> Boolean): Boolean {
    var allDoNotMatch = true
    forEach {
        if (lambda(it))
            allDoNotMatch = false
    }
    return allDoNotMatch
}

expect annotation class UiTest()