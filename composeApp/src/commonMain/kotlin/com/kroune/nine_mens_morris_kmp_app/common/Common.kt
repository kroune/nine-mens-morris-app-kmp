package com.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ButtonColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
 * The server's address
 */
val serverUrl
    get() = URLBuilder(host = "nine-men-s-morris.me")

/**
 * The API endpoint for user-related operations.
 */
val serverApi
    get() = serverUrl.apply {
        appendPathSegments("/api/v1/user")
    }

fun serverApi(modification: URLBuilder.() -> Unit): Url {
    return serverApi.apply {
        modification()
    }.build()
}

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
