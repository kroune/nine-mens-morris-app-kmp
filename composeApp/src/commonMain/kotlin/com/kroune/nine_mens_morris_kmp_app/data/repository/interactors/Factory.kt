package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors

import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountId.AccountIdInteractorI
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountId.AccountIdInteractorImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfo.AccountInfoInteractorI
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfo.AccountInfoInteractorImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.auth.AuthInteractorI
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.auth.AuthInteractorImpl
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtToken.JwtTokenInteractorI
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtToken.JwtTokenInteractorImpl

var jwtTokenRepositoryInteractor: JwtTokenInteractorI = JwtTokenInteractorImpl()

var accountInfoInteractor: AccountInfoInteractorI = AccountInfoInteractorImpl()

var authRepositoryInteractor: AuthInteractorI = AuthInteractorImpl()

var accountIdInteractor: AccountIdInteractorI = AccountIdInteractorImpl()