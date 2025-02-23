package io.github.kroune.nine_mens_morris_kmp_app.data

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRepositoryImpl

var authRepository: AuthRepositoryI = AuthRepositoryImpl()

var accountInfoRepository: AccountInfoRepositoryI = AccountInfoRepositoryImpl()

var jwtTokenDataSource: JwtTokenDataSourceI = JwtTokenDataSourceImpl()

var accountIdDataSource: AccountIdDataSourceI = AccountIdDataSourceImpl()

var onlineGameRepository: OnlineGameRepositoryI = OnlineGameRepositoryImpl()

var searchingForGameRepository: SearchingForGameRepositoryI = SearchingForGameRepositoryImpl()