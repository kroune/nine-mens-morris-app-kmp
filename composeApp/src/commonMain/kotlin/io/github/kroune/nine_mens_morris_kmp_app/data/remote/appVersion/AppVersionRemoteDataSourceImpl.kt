package io.github.kroune.nine_mens_morris_kmp_app.data.remote.appVersion

import io.github.kroune.nine_mens_morris_kmp_app.BuildKonfig
import io.github.kroune.nine_mens_morris_kmp_app.data.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments

class AppVersionRemoteDataSourceImpl : AppVersionRemoteDataSourceI {
    override suspend fun getLastAppVersion(): AppLastVersionApiResponse {
        val route = httpApi {
            appendPathSegments("version", "last-version")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("distribution", BuildKonfig.distribution)
                accept(ContentType.Application.Json)
            }
            AppLastVersionApiResponse.Success(
                request.body<Int?>()
            )
        }
            .recoverNetworkError(AppLastVersionApiResponse.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                AppLastVersionApiResponse.UnknownError()
            }
    }

    override suspend fun getRequiredVersion(): RequiredVersionApiResponse {
        val route = httpApi {
            appendPathSegments("version", "required-version")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("distribution", BuildKonfig.distribution)
                parameter("version", BuildKonfig.versionInt)
                accept(ContentType.Application.Json)
            }
            RequiredVersionApiResponse.Success(
                request.body()
            )
        }
            .recoverNetworkError(RequiredVersionApiResponse.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                RequiredVersionApiResponse.UnknownError()
            }
    }
}