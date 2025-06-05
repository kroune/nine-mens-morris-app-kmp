package io.github.kroune.unitTests.interactors

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtToken.JwtTokenInteractorImpl
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JwtTokenInteractorTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun test() {
        val testText = Uuid.random().toString()
        val local = object : JwtTokenDataSourceI {
            override fun getJwtToken(): String {
                return testText
            }

            override fun deleteJwtToken() {
                error("Not needed for test")
            }

            override fun updateJwtToken(newJwtToken: String) {
                error("Not needed for test")
            }
        }
        val remote = object : AuthRepositoryI {
            override suspend fun checkJwtToken(jwtToken: String): CheckJwtTokenApiResponses {
                error("Not needed for test")
            }

            override suspend fun login(login: String, password: String): LoginApiResponse {
                error("Not needed for test")
            }

            override suspend fun register(login: String, password: String): RegisterApiResponses {
                error("Not needed for test")
            }
        }
        val localId = object : AccountIdDataSourceI {
            override fun deleteAccountId() {
                error("Not needed for test")
            }

            override fun getAccountId(): Long? {
                error("Not needed for test")
            }

            override fun updateAccountId(newAccountId: Long) {
                error("Not needed for test")
            }

        }
        assertEquals(JwtTokenInteractorImpl(local, remote, localId).getJwtToken(), testText)
    }
}