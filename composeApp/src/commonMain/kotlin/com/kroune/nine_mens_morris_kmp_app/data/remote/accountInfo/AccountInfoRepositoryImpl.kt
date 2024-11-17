package com.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountIdByJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountPictureByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.CreationDateByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.LoginByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.RatingByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class AccountInfoRepositoryImpl : AccountInfoRepositoryI {
    override suspend fun getAccountRatingById(id: Long, jwtToken: String): Result<Long> {
        val route = "http${SERVER_ADDRESS}${USER_API}/get-rating-by-id"
        return runCatching {
            val request = network.get(route) {
                method = HttpMethod.Get
                url {
                    parameters["id"] = id.toString()
                    parameters["jwtToken"] = jwtToken
                }
            }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw RatingByIdApiResponses.ClientError
                        }

                        "no [id] parameter found" -> {
                            throw RatingByIdApiResponses.ClientError
                        }

                        "[id] parameter is not a long" -> {
                            throw RatingByIdApiResponses.ClientError
                        }

                        "[id] parameter is not valid" -> {
                            throw RatingByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw RatingByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw RatingByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<Long>(request.bodyAsText())
        }.recoverNetworkError(RatingByIdApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun getAccountCreationDateById(
        id: Long,
        jwtToken: String
    ): Result<Triple<Int, Int, Int>> {
        val route = "http${SERVER_ADDRESS}${USER_API}/get-creation-date-by-id"
        return runCatching {
            val request = network.get(route) {
                method = HttpMethod.Get
                url {
                    parameters["id"] = id.toString()
                    parameters["jwtToken"] = jwtToken
                }
            }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw CreationDateByIdApiResponses.ClientError
                        }

                        "no [id] parameter found" -> {
                            throw CreationDateByIdApiResponses.ClientError
                        }

                        "[id] parameter is not a long" -> {
                            throw CreationDateByIdApiResponses.ClientError
                        }

                        "[id] parameter is not valid" -> {
                            throw CreationDateByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw CreationDateByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw CreationDateByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<Triple<Int, Int, Int>>(request.bodyAsText())
        }.recoverNetworkError(CreationDateByIdApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun getAccountLoginById(id: Long, jwtToken: String): Result<String> {
        val route = "http${SERVER_ADDRESS}${USER_API}/get-login-by-id"
        return runCatching {
            val request = network.get(route) {
                method = HttpMethod.Get
                url {
                    parameters["id"] = id.toString()
                    parameters["jwtToken"] = jwtToken
                }
            }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw LoginByIdApiResponses.ClientError
                        }

                        "no [id] parameter found" -> {
                            throw LoginByIdApiResponses.ClientError
                        }

                        "[id] parameter is not a long" -> {
                            throw LoginByIdApiResponses.ClientError
                        }

                        "[id] parameter is not valid" -> {
                            throw LoginByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw LoginByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw LoginByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<String>(request.bodyAsText())
        }.recoverNetworkError(LoginByIdApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun getAccountPictureById(id: Long, jwtToken: String): Result<ByteArray> {
        val route = "http${SERVER_ADDRESS}${USER_API}/get-picture-by-id"
        return runCatching {
            val request =
                network.get(route) {
                    method = HttpMethod.Get
                    url {
                        parameters["id"] = id.toString()
                        parameters["jwtToken"] = jwtToken
                    }
                }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw AccountPictureByIdApiResponses.ClientError
                        }

                        "no [id] parameter found" -> {
                            throw AccountPictureByIdApiResponses.ClientError
                        }

                        "[id] parameter is not a long" -> {
                            throw AccountPictureByIdApiResponses.ClientError
                        }

                        "[id] parameter is not valid" -> {
                            throw AccountPictureByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw AccountPictureByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw AccountPictureByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<ByteArray>(request.bodyAsText())
        }.recoverNetworkError(AccountPictureByIdApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun getAccountIdByJwtToken(jwtToken: String): Result<Long> {
        val route = "http${SERVER_ADDRESS}${USER_API}/get-id-by-jwt-token"
        return runCatching {
            val request = network.get(route) {
                method = HttpMethod.Get
                url {
                    parameters["jwtToken"] = jwtToken
                }
            }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw AccountIdByJwtTokenApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw AccountIdByJwtTokenApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw AccountIdByJwtTokenApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<Long>(request.bodyAsText())
        }.recoverNetworkError(AccountIdByJwtTokenApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }
}
