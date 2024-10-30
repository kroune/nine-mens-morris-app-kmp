package com.kroune.nine_mens_morris_kmp_app.data.repository.source

import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId.AccountIdDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId.AccountIdDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.jwtToken.JwtTokenDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.jwtToken.JwtTokenDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.accountInfo.AccountInfoRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.accountInfo.AccountInfoRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth.AuthRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth.AuthRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame.OnlineGameRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.onlineGame.OnlineGameRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.searchingForGame.SearchingForGameRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.searchingForGame.SearchingForGameRepositoryImpl

var authRepository: AuthRepositoryI = AuthRepositoryImpl()

var accountInfoRepository: AccountInfoRepositoryI = AccountInfoRepositoryImpl()

var jwtTokenDataSource: JwtTokenDataSourceI = JwtTokenDataSourceImpl()

var accountIdDataSource: AccountIdDataSourceI = AccountIdDataSourceImpl()

var onlineGameRepository: OnlineGameRepositoryI = OnlineGameRepositoryImpl()

var searchingForGameRepository: SearchingForGameRepositoryI = SearchingForGameRepositoryImpl()