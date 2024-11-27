package com.kroune.nine_mens_morris_kmp_app.component

import com.arkivanov.decompose.ComponentContext

interface ComponentContextWithBackHandle: ComponentContext {
    fun onBackPressed()
}