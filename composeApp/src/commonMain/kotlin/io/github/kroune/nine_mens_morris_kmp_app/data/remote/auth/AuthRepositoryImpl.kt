package io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth

import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.common.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json

class AuthRepositoryImpl : AuthRepositoryI {
    override suspend fun register(login: String, password: String): RegisterApiResponses {
        val route = httpApi {
            appendPathSegments("reg")
        }
        return runCatching {
            val request = network.post(route) {
                parameter("login", login)
                parameter("password", password)
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
                val message = Json.decodeFromString<String>(request.bodyAsText())
                RegisterApiResponses.Success(message)
            }

            else -> {
                RegisterApiResponses.UnknownError()
            }
        }
    }

    override suspend fun login(login: String, password: String): LoginApiResponse {
        val route = httpApi {
            appendPathSegments("login")
            parameters["login"] = login
            parameters["password"] = password
        }
        return runCatching {
            val request = network.get(route)
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
                val jwtToken = Json.decodeFromString<String>(request.bodyAsText())
                LoginApiResponse.Success(jwtToken)
            }

            else -> {
                LoginApiResponse.UnknownError()
            }
        }
    }

    override suspend fun checkJwtToken(jwtToken: String): CheckJwtTokenApiResponses {
        val route = httpApi {
            appendPathSegments("check-jwt-token")
            parameters["jwtToken"] = jwtToken
        }
        return runCatching {
            val request = network.get(route)
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
                val result = Json.decodeFromString<Boolean>(request.bodyAsText())
                CheckJwtTokenApiResponses.Success(result)
            }

            else -> {
                CheckJwtTokenApiResponses.UnknownError()
            }
        }
    }
}
