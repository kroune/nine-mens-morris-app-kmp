package io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth

import io.github.kroune.nine_mens_morris_kmp_app.data.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments

class AuthRemoteDataSourceImpl : AuthRemoteDataSourceI {
    override suspend fun register(login: String, password: String): RegisterApiResponses {
        val route = httpApi {
            appendPathSegments("auth", "reg")
        }
        return runCatching {
            val request = network.post(route) {
                parameter("login", login)
                parameter("password", password)
                accept(ContentType.Application.ProtoBuf)
            }
            registerResult(request)
        }
            .recoverNetworkError(RegisterApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                RegisterApiResponses.UnknownError()
            }
    }

    private suspend fun registerResult(request: HttpResponse): RegisterApiResponses {
        return when (request.status) {
            HttpStatusCode.Conflict -> {
                RegisterApiResponses.LoginAlreadyInUse()
            }

            HttpStatusCode.InternalServerError -> {
                RegisterApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                RegisterApiResponses.Success(
                    request.body<String>()
                )
            }

            else -> {
                RegisterApiResponses.UnknownError()
            }
        }
    }

    override suspend fun login(login: String, password: String): LoginApiResponse {
        val route = httpApi {
            appendPathSegments("auth", "login")
            parameters["login"] = login
            parameters["password"] = password
        }
        return runCatching {
            val request = network.get(route) {
                accept(ContentType.Application.ProtoBuf)
            }
            loginResult(request)
        }
            .recoverNetworkError(LoginApiResponse.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse { LoginApiResponse.UnknownError() }
    }

    private suspend fun loginResult(request: HttpResponse): LoginApiResponse {
        return when (request.status) {
            HttpStatusCode.Unauthorized -> {
                LoginApiResponse.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                LoginApiResponse.ServerError()
            }

            HttpStatusCode.OK -> {
                LoginApiResponse.Success(
                    request.body<String>()
                )
            }

            else -> {
                LoginApiResponse.UnknownError()
            }
        }
    }

    override suspend fun checkJwtToken(jwtToken: String): CheckJwtTokenApiResponses {
        val route = httpApi {
            appendPathSegments("auth", "check-jwt-token")
            parameters["jwtToken"] = jwtToken
        }
        return runCatching {
            val request = network.get(route) {
                accept(ContentType.Application.ProtoBuf)
            }
            checkJwtTokenResult(request)
        }
            .recoverNetworkError(CheckJwtTokenApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                CheckJwtTokenApiResponses.UnknownError()
            }
    }

    private suspend fun checkJwtTokenResult(request: HttpResponse): CheckJwtTokenApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                CheckJwtTokenApiResponses.Success(false)
            }

            HttpStatusCode.InternalServerError -> {
                CheckJwtTokenApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                CheckJwtTokenApiResponses.Success(
                    request.body<Boolean>()
                )
            }

            else -> {
                CheckJwtTokenApiResponses.UnknownError()
            }
        }
    }
}
