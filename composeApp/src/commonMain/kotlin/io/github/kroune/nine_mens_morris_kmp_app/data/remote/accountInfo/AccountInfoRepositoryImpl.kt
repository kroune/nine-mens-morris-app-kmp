package io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.common.httpApi
import io.github.kroune.nine_mens_morris_kmp_app.common.network
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.Severity
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.logging.logOnFailure
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.recoverNetworkError
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json

class AccountInfoRepositoryImpl : AccountInfoRepositoryI {
    override suspend fun getAccountRatingById(id: Long, jwtToken: String): RatingByIdApiResponses {
        val route = httpApi {
            appendPathSegments("get-rating-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
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
                val result = Json.decodeFromString<Long>(request.bodyAsText())
                RatingByIdApiResponses.Success(result)
            }
        }
    }

    override suspend fun getAccountCreationDateById(
        id: Long,
        jwtToken: String,
    ): CreationDateByIdApiResponses {
        val route = httpApi {
            appendPathSegments("get-creation-date-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
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
                val decodedText = Json.decodeFromString<Triple<Int, Int, Int>>(request.bodyAsText())
                CreationDateByIdApiResponses.Success(decodedText)
            }

            else -> {
                CreationDateByIdApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getAccountLoginById(id: Long, jwtToken: String): LoginByIdApiResponses {
        val route = httpApi {
            appendPathSegments("get-login-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
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
                val result = Json.decodeFromString<String>(request.bodyAsText())
                LoginByIdApiResponses.Success(result)
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
            appendPathSegments("get-picture-by-id")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("id", id)
                parameter("jwtToken", jwtToken)
            }
            accountPictureByIdResult(request)
        }
            .recoverNetworkError(AccountPictureByIdApiResponses.NetworkError())
            .logOnFailure("exception in $route", severity = Severity.ERROR)
            .getOrElse {
                AccountPictureByIdApiResponses.UnknownError()
            }
    }

    private suspend fun accountPictureByIdResult(request: HttpResponse): AccountPictureByIdApiResponses {
        return when (request.status) {
            HttpStatusCode.Forbidden -> {
                AccountPictureByIdApiResponses.CredentialsError()
            }

            HttpStatusCode.InternalServerError -> {
                AccountPictureByIdApiResponses.ServerError()
            }

            HttpStatusCode.OK -> {
                val decodedMessage = Json.decodeFromString<ByteArray>(request.bodyAsText())
                AccountPictureByIdApiResponses.Success(decodedMessage)
            }

            else -> {
                AccountPictureByIdApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getAccountIdByJwtToken(jwtToken: String): AccountIdByJwtTokenApiResponses {
        val route = httpApi {
            appendPathSegments("get-id-by-jwt-token")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("jwtToken", jwtToken)
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
                val result = Json.decodeFromString<Long>(request.bodyAsText())
                AccountIdByJwtTokenApiResponses.Success(result)
            }

            else -> {
                AccountIdByJwtTokenApiResponses.UnknownError()
            }
        }
    }

    override suspend fun getLeaderboard(amount: Int, jwtToken: String): LeaderboardApiResponses {
        val route = httpApi {
            appendPathSegments("leaderboard")
        }
        return runCatching {
            val request = network.get(route) {
                parameter("jwtToken", jwtToken)
                parameter("amount", amount)
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
                val decodedMessage = Json.decodeFromString<List<Long>>(request.bodyAsText())
                LeaderboardApiResponses.Success(decodedMessage)
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
            appendPathSegments("upload-picture")
            parameters["jwtToken"] = jwtToken
        }
        return runCatching {
            val request = network.post(route) {
                setBody<ByteArray>(picture)
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
