package io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging

fun log(message: String, throwable: Throwable? = null, severity: Severity) {
    buildString {
        appendLine(message)
        throwable?.let {
            appendLine("exception ${it.stackTraceToString()}")
        }
    }.let {
        println(it)
    }
}

fun <T> Result<T>.logOnFailure(message: String, severity: Severity): Result<T> {
    return this.onFailure {
        log(message, throwable = it, severity)
    }
}

enum class Severity {
    DEBUG,
    INFO,
    ERROR,
    TRACE
}