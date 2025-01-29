package io.github.kroune.unitTests.interactors

import com.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI
import com.kroune.nine_mens_morris_kmp_app.interactors.jwtToken.JwtTokenInteractorImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JwtTokenInteractorTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun test() {
        val testText = Uuid.random().toString()
        val local = object: JwtTokenDataSourceI {
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
        val remote = object: AuthRepositoryI {
            override suspend fun checkJwtToken(jwtToken: String): Result<Boolean> {
                error("Not needed for test")
            }

            override suspend fun login(login: String, password: String): Result<String> {
                error("Not needed for test")
            }

            override suspend fun register(login: String, password: String): Result<String> {
                error("Not needed for test")
            }
        }
        val localId = object: AccountIdDataSourceI {
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