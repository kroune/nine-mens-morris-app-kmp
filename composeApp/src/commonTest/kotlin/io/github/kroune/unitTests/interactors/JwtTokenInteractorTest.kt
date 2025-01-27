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
                TODO("Not yet implemented")
            }

            override fun updateJwtToken(newJwtToken: String) {
                TODO("Not yet implemented")
            }
        }
        val remote = object: AuthRepositoryI {
            override suspend fun checkJwtToken(jwtToken: String): Result<Boolean> {
                TODO("Not yet implemented")
            }

            override suspend fun login(login: String, password: String): Result<String> {
                TODO("Not yet implemented")
            }

            override suspend fun register(login: String, password: String): Result<String> {
                TODO("Not yet implemented")
            }
        }
        val localId = object: AccountIdDataSourceI {
            override fun deleteAccountId() {
                TODO("Not yet implemented")
            }

            override fun getAccountId(): Long? {
                TODO("Not yet implemented")
            }

            override fun updateAccountId(newAccountId: Long) {
                TODO("Not yet implemented")
            }

        }
        assertEquals(JwtTokenInteractorImpl(local, remote, localId).getJwtToken(), testText)
    }
}