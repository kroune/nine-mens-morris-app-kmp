package com.kroune.nine_mens_morris_kmp_app.data

import com.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRepositoryImpl

var authRepository: AuthRepositoryI = AuthRepositoryImpl()

var accountInfoRepository: AccountInfoRepositoryI = AccountInfoRepositoryImpl()

var jwtTokenDataSource: JwtTokenDataSourceI = JwtTokenDataSourceImpl()

var accountIdDataSource: AccountIdDataSourceI = AccountIdDataSourceImpl()

var onlineGameRepository: OnlineGameRepositoryI = OnlineGameRepositoryImpl()

var searchingForGameRepository: SearchingForGameRepositoryI = SearchingForGameRepositoryImpl()