package com.kroune.nine_mens_morris_kmp_app.navigation

object BackHandler {
    private var lambda = {}
    fun setCallbackAction(lambdaValue: () -> Unit) {
        lambda = lambdaValue
    }

    fun onCallback() {
        lambda()
    }
}