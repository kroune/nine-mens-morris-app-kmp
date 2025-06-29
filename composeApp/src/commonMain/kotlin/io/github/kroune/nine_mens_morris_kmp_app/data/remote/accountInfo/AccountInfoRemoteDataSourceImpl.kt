package io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.data.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.data.network
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class AccountInfoRemoteDataSourceImpl : AccountInfoRemoteDataSourceI {
    override suspend fun getAccountRatingById(id: Long, jwtToken: String): RatingByIdApiResponses {
        val route = httpApi {
            appendPathSegments("user", "get-rating-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
                accept(ContentType.Application.ProtoBuf)
            }
            accountRatingByIdResult(request)
        }
            .recoverNetworkError(RatingByIdApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                RatingByIdApiResponses.UnknownError()
            }
    }

    suspend fun accountRatingByIdResult(request: HttpResponse): RatingByIdApiResponses {
        return when (request.status) {
            HttpStatusCode.BadRequest -> {
                RatingByIdApiResponses.UnknownError()
            }

            HttpStatusCode.Forbidden -> {
                RatingByIdApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                RatingByIdApiResponses.ServerError()
            }

            else -> {
                RatingByIdApiResponses.Success(
                    request.body<Long>()
                )
            }
        }
    }

    override suspend fun getAccountCreationDateById(
        id: Long,
        jwtToken: String,
    ): CreationDateByIdApiResponses {
        val route = httpApi {
            appendPathSegments("user", "get-creation-date-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
                accept(ContentType.Application.ProtoBuf)
            }
            accountCreationDateByIdResult(request)
        }
            .recoverNetworkError(CreationDateByIdApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                CreationDateByIdApiResponses.UnknownError()
            }
    }

    suspend fun accountCreationDateByIdResult(request: HttpResponse): CreationDateByIdApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                CreationDateByIdApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                CreationDateByIdApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                CreationDateByIdApiResponses.Success(
                    request.body<Triple<Int, Int, Int>>()
                )
            }

            else -> {
                CreationDateByIdApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getAccountLoginById(id: Long, jwtToken: String): LoginByIdApiResponses {
        val route = httpApi {
            appendPathSegments("user", "get-login-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
                accept(ContentType.Application.ProtoBuf)
            }
            accountLoginByIdResult(request)
        }
            .recoverNetworkError(LoginByIdApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse { LoginByIdApiResponses.UnknownError() }
    }

    suspend fun accountLoginByIdResult(request: HttpResponse): LoginByIdApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                LoginByIdApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                LoginByIdApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                LoginByIdApiResponses.Success(
                    request.body<String>()
                )
            }

            else -> {
                LoginByIdApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getAccountPictureById(
        id: Long,
        jwtToken: String
    ): AccountPictureByIdApiResponses {
        val route = httpApi {
            appendPathSegments("user", "get-picture-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
                accept(ContentType.Application.ProtoBuf)
            }
            accountPictureByIdResult(request)
        }
            .recoverNetworkError(AccountPictureByIdApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                AccountPictureByIdApiResponses.UnknownError()
            }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun accountPictureByIdResult(request: HttpResponse): AccountPictureByIdApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                AccountPictureByIdApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                AccountPictureByIdApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                // FIXME
                // https://youtrack.jetbrains.com/issue/KTOR-8626/Content-Negotiation-doesnt-work-properly-with-ByteArray
                val decodedMessage = ProtoBuf.decodeFromByteArray<ByteArray>(request.body())
                AccountPictureByIdApiResponses.Success(decodedMessage)
            }

            else -> {
                AccountPictureByIdApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getAccountIdByJwtToken(jwtToken: String): AccountIdByJwtTokenApiResponses {
        val route = httpApi {
            appendPathSegments("user", "get-id-by-jwt-token")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("jwtToken", jwtToken)
                accept(ContentType.Application.ProtoBuf)
            }
            accountIdByJwtTokenResult(request)
        }
            .recoverNetworkError(AccountIdByJwtTokenApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                AccountIdByJwtTokenApiResponses.UnknownError()
            }
    }

    suspend fun accountIdByJwtTokenResult(request: HttpResponse): AccountIdByJwtTokenApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                AccountIdByJwtTokenApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                AccountIdByJwtTokenApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                AccountIdByJwtTokenApiResponses.Success(
                    request.body<Long>()
                )
            }

            else -> {
                AccountIdByJwtTokenApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getLeaderboard(amount: Int, jwtToken: String): LeaderboardApiResponses {
        val route = httpApi {
            appendPathSegments("user", "leaderboard")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("jwtToken", jwtToken)
                parameter("amount", amount)
                accept(ContentType.Application.ProtoBuf)
            }
            leaderboardResult(request)
        }
            .recoverNetworkError(LeaderboardApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                LeaderboardApiResponses.UnknownError()
            }
    }

    suspend fun leaderboardResult(request: HttpResponse): LeaderboardApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                LeaderboardApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                LeaderboardApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                LeaderboardApiResponses.Success(
                    request.body<List<Long>>()
                )
            }

            else -> {
                LeaderboardApiResponses.UnknownError()
            }
        }
    }

    override suspend fun uploadPicture(
        picture: ByteArray,
        jwtToken: String
    ): UploadPictureApiResponses {
        val route = httpApi {
            appendPathSegments("user", "upload-picture")
            parameters["jwtToken"] = jwtToken
        }
        return runCatching {
            val request = network.post(route) {
                setBody<ByteArray>(picture)
                accept(ContentType.Application.ProtoBuf)
            }
            uploadPictureResult(request)
        }
            .recoverNetworkError(UploadPictureApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse { UploadPictureApiResponses.UnknownError() }
    }

    suspend fun uploadPictureResult(request: HttpResponse): UploadPictureApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                UploadPictureApiResponses.CredentialsError()
            }

            HttpStatusCode.BadRequest -> {
                val text = request.bodyAsText()
                val maxWidth = text.substringBefore("x").toIntOrNull()
                val maxHeight = text.substringBefore("x").toIntOrNull()
                if (text.any { it == 'x' } && maxWidth != null && maxHeight != null)
                    UploadPictureApiResponses.TooLargeImage(maxWidth, maxHeight)
                else
                    UploadPictureApiResponses.UnknownError()
            }

            HttpStatusCode.InternalServerError -> {
                UploadPictureApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                UploadPictureApiResponses.Success()
            }

            else -> {
                UploadPictureApiResponses.UnknownError()
            }
        }
    }
}
