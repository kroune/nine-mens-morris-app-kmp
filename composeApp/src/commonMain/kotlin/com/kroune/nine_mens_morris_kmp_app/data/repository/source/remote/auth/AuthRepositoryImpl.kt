package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.auth

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.CreationDateByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RegisterApiResponses
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class AuthRepositoryImpl : AuthRepositoryI {
    override suspend fun register(login: String, password: String): Result<String> {
        return runCatching {
            val request =
                network.get("http${SERVER_ADDRESS}${USER_API}/reg") {
                    method = HttpMethod.Post
                    url {
                        parameters["login"] = login
                        parameters["password"] = password
                    }
                }
            when (request.status) {
                HttpStatusCode.Conflict -> {
                    when (request.bodyAsText()) {
                        "[login] is already in use" -> {

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

                        "[id] parameter is not a long" -> {
                            throw RegisterApiResponses.ClientError
                        }
                        "[id] parameter is not valid" -> {
                            throw RegisterApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw RegisterApiResponses.CredentialsError
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
        }.onFailure {
            println("error accessing ${"http${SERVER_ADDRESS}${USER_API}/reg"}")
            it.printStackTrace()
        }
    }

    override suspend fun login(login: String, password: String): Result<String> {
        return runCatching {
            val result =
                network.get("http${SERVER_ADDRESS}${USER_API}/login") {
                    method = HttpMethod.Get
                    url {
                        parameters["login"] = login
                        parameters["password"] = password
                    }
                }
            Json.decodeFromString<String>(result.bodyAsText())
        }.onFailure {
            println("error accessing ${"http${SERVER_ADDRESS}${USER_API}/login"}")
            it.printStackTrace()
        }
    }

    override suspend fun checkJwtToken(jwtToken: String): Result<Boolean> {
        return runCatching {
            val result = network.get("http$SERVER_ADDRESS$USER_API/check-jwt-token") {
                method = HttpMethod.Get
                url {
                    parameters["jwtToken"] = jwtToken
                }
            }
            Json.decodeFromString<Boolean>(result.bodyAsText())
        }.onFailure {
            println("error checking jwt token")
            it.printStackTrace()
        }
    }
}