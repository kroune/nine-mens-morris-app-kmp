package io.github.kroune.nine_mens_morris_kmp_app

actual inline fun <T> Result<T>.onNativeNetworkError(lambda: (Throwable) -> Unit) {
    onFailure {
        if (it is java.io.IOException || it is java.nio.channels.UnresolvedAddressException) {
            lambda(it)
            return
        }
    }
}
