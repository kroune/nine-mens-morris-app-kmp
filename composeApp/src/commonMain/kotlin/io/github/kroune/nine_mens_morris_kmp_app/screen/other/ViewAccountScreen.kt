package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.padding2
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawAccountCreationDate
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawRating
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewAccountScreen(
    onEvent: (ViewAccountScreenEvent) -> Unit,
    state: ViewAccountScreenState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val snackbarHostState = remember { SnackbarHostState() }
    with(state) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = padding2)
                    .padding(top = padding2),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    BoxWithConstraints {
                        val size = min(this.maxWidth, this.maxHeight) / 2
                        with(sharedTransitionScope) {
                            val key = rememberSharedContentState(key = "icon-${state.accountId}")
                            DrawIcon(
                                Modifier
                                    .sharedElement(
                                        key,
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .size(size)
                                    .aspectRatio(1f, true),
                                pictureByteArray = accountPictureResult,
                                onReload = { onEvent(ViewAccountScreenEvent.ReloadIcon) },
                                onClick = {},
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                    with(sharedTransitionScope) {
                        val key = rememberSharedContentState(key = "name-${state.accountId}")
                        DrawName(
                            modifier = Modifier
                                .sharedElement(
                                    key,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                .fillMaxWidth()
                                .height(50.dp),
                            onSuccess = @Composable {
                                Text(
                                    it,
                                    fontSize = 30.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            accountName = accountLoginResult,
                            onReload = { onEvent(ViewAccountScreenEvent.ReloadName) },
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
                with(sharedTransitionScope) {
                    val key = rememberSharedContentState(key = "rating-${state.accountId}")
                    DrawRating(
                        modifier = Modifier
                            .sharedElement(
                                key,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .fillMaxWidth(0.5f)
                            .height(30.dp),
                        onSuccess = {
                            Text(
                                "${stringResource(Res.string.rating)}: $it",
                                fontSize = 20.sp
                            )
                        },
                        accountRating = accountRatingResult,
                        onReload = { onEvent(ViewAccountScreenEvent.ReloadRating) },
                        snackbarHostState = snackbarHostState
                    )
                }
                DrawAccountCreationDate(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(30.dp),
                    onSuccess = { (first, second, third) ->
                        Text(
                            "$first.$second.$third",
                            fontSize = 20.sp
                        )
                    },
                    accountCreationDate = accountCreationDateResult,
                    onReload = {
                        onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                    },
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}
