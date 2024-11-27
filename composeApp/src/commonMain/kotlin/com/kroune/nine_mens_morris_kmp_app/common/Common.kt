package com.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountPictureByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.CreationDateByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.LoginByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.RatingByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.client_error
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.retry
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class BlackGrayColors : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Black)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Gray)
    }
}

class TransparentColors : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }
}

val network = HttpClient() {
    install(HttpRequestRetry) {
        // retry on timeout
        retryIf(maxRetries = 5) { request, response ->
            response.status.value == 408
        }
        retryOnExceptionIf(maxRetries = 5) { request, exception ->
            exception is HttpRequestTimeoutException
        }
        exponentialDelay()
    }
    install(HttpTimeout) {
        this.requestTimeoutMillis = 10 * 1000
        this.socketTimeoutMillis = 30 * 60 * 1000
        this.connectTimeoutMillis = 10 * 1000
    }
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingInterval = 3.seconds
    }
}

/**
 * The server's address.
 * 10.0.2.2:8080 for testing on androids
 * 0.0.0.0:8080 for testing on other targets
 */
const val SERVER_ADDRESS = "://nine-men-s-morris.me"

/**
 * The API endpoint for user-related operations.
 */
const val USER_API = "/api/v1/user"

/**
 * a simple infinite loading animation
 */
@Composable
fun LoadingCircle() {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    CircularProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier
            .aspectRatio(1f)
    )
}

val triangleShape: TriangleShape = TriangleShape()

/**
 * triangle shape
 * looks like this
 * -------
 *   -----
 *     ---
 *       -
 */
class TriangleShape : Shape {
    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val x = size.width
                val y = size.height

                moveTo(0f, 0f)
                lineTo(x, 0f)
                lineTo(x, y)
            }
        )
    }
}

/**
 * parallelogram shape
 */
class ParallelogramShape(
    private val bottomLineLeftOffset: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val x = size.width
                val y = size.height

                // top line
                moveTo(0f, 0f)
                lineTo(x, 0f)
                // bottom line
                lineTo(x, y)
                lineTo(x - bottomLineLeftOffset, y)
            }
        )
    }
}

@Composable
inline fun AppTheme(function: BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7E7E7E))
    ) {
        function()
    }
}


@Throws(
    ClosedReceiveChannelException::class,
    CancellationException::class,
)
suspend inline fun ReceiveChannel<Frame>.receiveText(): String {
    val input = this.receive() as Frame.Text
    return input.readText()
}

@Throws(
    ClosedReceiveChannelException::class,
    CancellationException::class,
    SerializationException::class,
    IllegalArgumentException::class
)
suspend inline fun <reified T> ReceiveChannel<Frame>.receiveDeserialized(): T {
    val input = this.receive() as Frame.Text
    return Json.decodeFromString<T>(input.readText())
}

@Throws(
    ClosedSendChannelException::class,
    CancellationException::class
)
suspend inline fun <reified T> SendChannel<Frame>.sendSerialized(value: T) {
    val text = Json.encodeToString<T>(value)
    this.send(Frame.Text(text))
}

@Throws(
    ClosedReceiveChannelException::class,
    CancellationException::class,
)
suspend inline fun DefaultClientWebSocketSession.receiveText(): String {
    return this.incoming.receiveText()
}

suspend inline fun DefaultClientWebSocketSession.receiveTextCatching(): Result<String> {
    return runCatching {
        this.incoming.receiveText()
    }
}

@Throws(
    ClosedReceiveChannelException::class,
    CancellationException::class,
    SerializationException::class,
    IllegalArgumentException::class
)
suspend inline fun <reified T> DefaultClientWebSocketSession.receiveDeserialized(): T {
    return this.incoming.receiveDeserialized<T>()
}

suspend inline fun <reified T> DefaultClientWebSocketSession.receiveDeserializedCatching(): Result<T> {
    return runCatching {
        this.incoming.receiveDeserialized<T>()
    }
}

@Throws(ClosedSendChannelException::class, CancellationException::class)
suspend inline fun <reified T> DefaultClientWebSocketSession.sendSerialized(value: T) {
    this.outgoing.sendSerialized(value)
}

suspend inline fun <reified T> DefaultClientWebSocketSession.sendSerializedCatching(value: T): Throwable? {
    return runCatching {
        this.outgoing.sendSerialized(value)
    }.exceptionOrNull()
}

/**
 * draws user rating
 */
@Composable
fun DrawRating(
    text: @Composable (Long) -> @Composable Unit,
    accountRating: Result<Long>?,
    reloadRating: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    AnimatedContent(
        accountRating,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(3000)
            ) togetherWith fadeOut(animationSpec = tween(3000))
        },
        contentAlignment = Alignment.Center
    ) {
        when {
            it == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LoadingCircle()
                }
            }

            it.isSuccess -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, top = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    text(it.getOrThrow())
                }
            }

            it.isFailure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, top = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = {
                        reloadRating()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            modifier = Modifier
                                .padding(start = 12.dp, top = 12.dp)
                                .fillMaxHeight(0.1f)
                                .clip(CircleShape)
                        )
                    }
                }
                val exception = it.exceptionOrNull()
                val exceptionText: String = when (exception) {
                    !is RatingByIdApiResponses -> {
                        stringResource(Res.string.unknown_error)
                    }

                    is RatingByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    RatingByIdApiResponses.ClientError -> {
                        stringResource(Res.string.client_error)
                    }

                    RatingByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    RatingByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    else -> {
                        error("kotlin broke")
                    }
                }
                val retryText = stringResource(Res.string.retry)
                scope.launch {
                    snackbarHostState.showSnackbar(exceptionText, retryText).let { snackbarResult ->
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            reloadRating()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawAccountCreationDate(
    text: @Composable (Triple<Int, Int, Int>) -> String,
    accountCreationDate: Result<Triple<Int, Int, Int>>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountCreationDate == null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountCreationDate.isSuccess -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text(accountCreationDate.getOrThrow()),
                    fontSize = 20.sp
                )
            }
        }

        accountCreationDate.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .padding(start = 12.dp, top = 12.dp)
                            .fillMaxHeight(0.1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountCreationDate.exceptionOrNull()
            val exceptionText: String = when (exception) {
                !is CreationDateByIdApiResponses -> {
                    stringResource(Res.string.unknown_error)
                }

                is CreationDateByIdApiResponses.NetworkError -> {
                    stringResource(Res.string.network_error)
                }

                CreationDateByIdApiResponses.ClientError -> {
                    stringResource(Res.string.client_error)
                }

                CreationDateByIdApiResponses.CredentialsError -> {
                    stringResource(Res.string.credentials_error)
                }

                CreationDateByIdApiResponses.ServerError -> {
                    stringResource(Res.string.server_error)
                }

                else -> {
                    error("kotlin broke")
                }
            }
            val retryText = stringResource(Res.string.retry)
            scope.launch {
                snackbarHostState.showSnackbar(exceptionText, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawIcon(
    modifier: Modifier = Modifier,
    pictureByteArray: Result<ByteArray>?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    AnimatedContent(
        pictureByteArray,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(450))
        }
    ) {
        when {
            it == null -> {
                Box(
                    modifier = modifier
                        .padding(start = 12.dp, top = 12.dp)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingCircle()
                }
            }

            it.isSuccess -> {
                Image(
                    bitmap = it.getOrThrow().decodeToImageBitmap(),
                    contentDescription = "Profile icon",
                    modifier = modifier
                        .padding(start = 12.dp, top = 12.dp)
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                )
            }

            it.isFailure -> {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(start = 12.dp, top = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = {
                        onReload()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            modifier = modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .clip(CircleShape)
                        )
                    }
                }
                val exception = it.exceptionOrNull()
                val text: String = when (exception) {
                    !is AccountPictureByIdApiResponses -> {
                        stringResource(Res.string.unknown_error)
                    }

                    is AccountPictureByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    AccountPictureByIdApiResponses.ClientError -> {
                        stringResource(Res.string.client_error)
                    }

                    AccountPictureByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    AccountPictureByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    else -> {
                        error("kotlin broke")
                    }
                }
                val retryText = stringResource(Res.string.retry)
                scope.launch {
                    snackbarHostState.showSnackbar(text, retryText).let {
                        if (it == SnackbarResult.ActionPerformed) {
                            onReload()
                        }
                    }
                }
            }
        }
    }
}


/**
 * draws user name or loading animation
 */
@Composable
fun DrawName(
    text: (String) -> String,
    accountName: Result<String>?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountName == null -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountName.isSuccess -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text(accountName.getOrThrow()), fontSize = 20.sp)
            }
        }

        accountName.isFailure -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .fillMaxHeight(0.75f)
                    .aspectRatio(1f)
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onReload()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountName.exceptionOrNull()
            val exceptionText: String = when (exception) {
                !is LoginByIdApiResponses -> {
                    stringResource(Res.string.unknown_error)
                }

                is LoginByIdApiResponses.NetworkError -> {
                    stringResource(Res.string.network_error)
                }

                LoginByIdApiResponses.ClientError -> {
                    stringResource(Res.string.client_error)
                }

                LoginByIdApiResponses.CredentialsError -> {
                    stringResource(Res.string.credentials_error)
                }

                LoginByIdApiResponses.ServerError -> {
                    stringResource(Res.string.server_error)
                }

                else -> {
                    error("kotlin broke")
                }
            }
            val retryText = stringResource(Res.string.retry)
            scope.launch {
                snackbarHostState.showSnackbar(exceptionText, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onReload()
                    }
                }
            }
        }
    }
}

@Composable
fun BackHandler(backHandler: BackHandler, isEnabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val callback =
        remember {
            BackCallback(isEnabled = isEnabled) {
                currentOnBack()
            }
        }
    SideEffect { callback.isEnabled = isEnabled }
    DisposableEffect(backHandler) {
        backHandler.register(callback)
        onDispose { backHandler.unregister(callback) }
    }
}
