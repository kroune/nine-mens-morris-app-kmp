package com.kroune.nine_mens_morris_kmp_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform