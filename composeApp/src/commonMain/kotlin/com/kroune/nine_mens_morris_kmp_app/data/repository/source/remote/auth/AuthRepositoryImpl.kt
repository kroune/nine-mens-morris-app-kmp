package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.CheckJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.LoginApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RegisterApiResponses
import com.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class AuthRepositoryImpl : AuthRepositoryI {
    override suspend fun register(login: String, password: String): Result<String> {
        val route = "http${SERVER_ADDRESS}${USER_API}/reg"
        return runCatching {
            val request =
                network.get(route) {
                    method = HttpMethod.Post
                    url {
                        parameters["login"] = login
                        parameters["password"] = password
                    }
                }
            when (request.status) {
                HttpStatusCode.Conflict -> {
                    when (request.bodyAsText()) {
                        "login is already in use" -> {
                            throw RegisterApiResponses.LoginAlreadyInUse
                        }
                    }
                }

                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [login] parameter found" -> {
                            throw RegisterApiResponses.ClientError
                        }

                        "no [password] parameter found" -> {
                            throw RegisterApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw RegisterApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<String>(request.bodyAsText())
        }.recoverNetworkError(RegisterApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun login(login: String, password: String): Result<String> {
        val route = "http${SERVER_ADDRESS}${USER_API}/login"
        return runCatching {
            val request =
                network.get(route) {
                    method = HttpMethod.Get
                    url {
                        parameters["login"] = login
                        parameters["password"] = password
                    }
                }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [login] parameter found" -> {
                            throw LoginApiResponses.ClientError
                        }

                        "no [password] parameter found" -> {
                            throw LoginApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "login + password aren't present in the db" -> {
                            throw LoginApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw LoginApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<String>(request.bodyAsText())
        }.recoverNetworkError(LoginApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }

    override suspend fun checkJwtToken(jwtToken: String): Result<Boolean> {
        val route = "http${SERVER_ADDRESS}${USER_API}/check-jwt-token"
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
                            throw CheckJwtTokenApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            return@runCatching false
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw CheckJwtTokenApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<Boolean>(request.bodyAsText())
        }.recoverNetworkError(CheckJwtTokenApiResponses.NetworkError).onFailure {
            println("exception in $route - ${it.printStackTrace()}")
        }
    }
}