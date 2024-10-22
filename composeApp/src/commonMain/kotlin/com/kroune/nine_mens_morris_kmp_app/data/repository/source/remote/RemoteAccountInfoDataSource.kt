package com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote

import com.kroune.nine_mens_morris_kmp_app.common.SERVER_ADDRESS
import com.kroune.nine_mens_morris_kmp_app.common.USER_API
import com.kroune.nine_mens_morris_kmp_app.common.network
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class RemoteAccountInfoDataSource {
    suspend fun getAccountRatingById(id: Long, jwtToken: String): Result<Long> {
        return runCatching {
            val request = network.get("http${SERVER_ADDRESS}${USER_API}/get-rating-by-id") {
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
        }
    }

    suspend fun getAccountCreationDateById(id: Long, jwtToken: String): Result<Triple<Int, Int, Int>> {
        return runCatching {
            val request = network.get("http${SERVER_ADDRESS}${USER_API}/get-creation-date-by-id") {
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
        }
    }

    suspend fun getAccountLoginById(id: Long, jwtToken: String): Result<String> {
        return runCatching {
            val request = network.get("http${SERVER_ADDRESS}${USER_API}/get-login-by-id") {
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
        }
    }

    suspend fun getAccountPictureById(id: Long, jwtToken: String): Result<ByteArray> {
        return runCatching {
            val request =
                network.get("http${SERVER_ADDRESS}${USER_API}/get-picture-by-id") {
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
                            throw PictureByIdApiResponses.ClientError
                        }

                        "no [id] parameter found" -> {
                            throw PictureByIdApiResponses.ClientError
                        }

                        "[id] parameter is not a long" -> {
                            throw PictureByIdApiResponses.ClientError
                        }
                        "[id] parameter is not valid" -> {
                            throw PictureByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw PictureByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw PictureByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<ByteArray>(request.bodyAsText())
        }
    }

    suspend fun getAccountIdByJwtToken(jwtToken: String): Result<Long> {
        return runCatching {
            val request = network.get("http${SERVER_ADDRESS}${USER_API}/get-id-by-jwt-token") {
                method = HttpMethod.Get
                url {
                    parameters["jwtToken"] = jwtToken
                }
            }
            when (request.status) {
                HttpStatusCode.BadRequest -> {
                    when (request.bodyAsText()) {
                        "no [jwtToken] parameter found" -> {
                            throw PictureByIdApiResponses.ClientError
                        }
                    }
                }

                HttpStatusCode.Forbidden -> {
                    when (request.bodyAsText()) {
                        "[jwtToken] parameter is not valid" -> {
                            throw PictureByIdApiResponses.CredentialsError
                        }
                    }
                }

                HttpStatusCode.InternalServerError -> {
                    when (request.bodyAsText()) {
                        "Internal server error" -> {
                            throw PictureByIdApiResponses.ServerError
                        }
                    }
                }
            }
            Json.decodeFromString<Long>(request.bodyAsText())
        }
    }
}
