package io.github.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

val network = HttpClient {
    install(HttpRequestRetry) {
        // retry on timeout
        retryIf(maxRetries = 5) { _, response ->
            response.status.value == 408
        }
        retryOnExceptionIf(maxRetries = 5) { _, exception ->
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
private val serverApi
    get() = serverUrl.apply {
        appendPathSegments("/api/v1/user")
    }

fun httpApi(modification: URLBuilder.() -> Unit): Url {
    return serverApi.apply {
        protocol = URLProtocol.HTTPS
        modification()
    }.build()
}

fun wsApi(modification: URLBuilder.() -> Unit): Url {
    return serverApi.apply {
        protocol = URLProtocol.WSS
        modification()
    }.build()
}

/**
 * a simple infinite loading animation
 */
@Composable
fun LoadingCircle(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis, easing = LinearEasing
            ), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    CircularProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .aspectRatio(1f)
    )
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

@Composable
fun <T> StateFlow<T>.collectValue(
    context: CoroutineContext = EmptyCoroutineContext
): T = collectAsState(
    context = context
).value

@Serializable
data class ServerEvent(
    val data: ByteArray,
    val metadata: ByteArray
)

inline fun <reified A, reified B> Frame.decodeServerEvent(): Pair<A, B> {
    return data.decodeProtobuf<ServerEvent>().let { (data, metadata) ->
        data.decodeProtobuf<A>() to metadata.decodeProtobuf<B>()
    }
}

inline fun <reified A> ByteArray.decodeProtobuf(): A {
    return ProtoBuf.decodeFromByteArray(this)
}

inline fun <T, R> StateFlow<T>.map(
    scope: CoroutineScope,
        crossinline transform: (value: T) -> R
): StateFlow<R> {
    val stateFlow = MutableStateFlow(transform(value))
    scope.launch {
        this@map.collect {
            stateFlow.emit(transform(it))
        }
    }
    return stateFlow
}