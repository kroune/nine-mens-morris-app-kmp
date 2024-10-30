package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.onlineGame

interface OnlineGameInteractorI {
    suspend fun connect(gameId: Long)
    suspend fun giveUp()
}