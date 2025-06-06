package io.github.kroune.nine_mens_morris_kmp_app.di

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenRemoteDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRemoteDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.onlineGame.OnlineGameRemoteDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.searchingForGame.SearchingForGameRemoteDataSourceImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountId.AccountIdRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountId.AccountIdRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo.AccountInfoRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.auth.AuthRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.auth.AuthRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.onlineGame.OnlineGameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.onlineGame.OnlineGameRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.repositories.searchingForGame.SearchingForGameRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.searchingForGame.SearchingForGameRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<SearchingForGameRepositoryI> {
        SearchingForGameRepositoryImpl(get(), get())
    }
    single<SearchingForGameRemoteDataSourceImpl> { SearchingForGameRemoteDataSourceImpl() }

    single<AccountInfoRemoteDataSourceI> { AccountInfoRemoteDataSourceImpl() }
    single<AccountInfoRepositoryI> {
        AccountInfoRepositoryImpl(get(), get())
    }

    single<JwtTokenRemoteDataSourceI> { JwtTokenRemoteDataSourceImpl() }
    single<JwtTokenRepositoryI> { JwtTokenRepositoryImpl(get(), get(), get()) }

    single<AccountIdLocalDataSourceI> { AccountIdLocalDataSourceImpl() }
    single<AccountIdRepositoryI> {
        AccountIdRepositoryImpl(get(), get(), get())
    }

    single<OnlineGameRemoteDataSourceI> { OnlineGameRemoteDataSourceImpl() }
    single<OnlineGameRepositoryI> { OnlineGameRepositoryImpl(get(), get()) }

    single<AuthRemoteDataSourceI> { AuthRemoteDataSourceImpl() }
    single<AuthRepositoryI> {
        AuthRepositoryImpl(get(), get(), get())
    }
}