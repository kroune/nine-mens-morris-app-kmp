package com.kroune.nine_mens_morris_kmp_app.data.repository.source

import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId.AccountIdDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId.AccountIdDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.jwtToken.JwtTokenDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.jwtToken.JwtTokenDataSourceImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.accountInfo.AccountInfoRepositoryImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.accountInfo.AccountInfoRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth.AuthRepositoryI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth.AuthRepositoryImpl

val authRepository: AuthRepositoryI = AuthRepositoryImpl()

val accountInfoRepository: AccountInfoRepositoryI = AccountInfoRepositoryImpl()

val jwtTokenDataSource: JwtTokenDataSourceI = JwtTokenDataSourceImpl()

val accountIdDataSource: AccountIdDataSourceI = AccountIdDataSourceImpl()