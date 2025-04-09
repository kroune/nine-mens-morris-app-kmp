package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.event.other.ViewAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawAccountCreationDate
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewAccountScreen(
    component: ViewAccountScreenComponent
) {
    val name = component.accountName
    val rating = component.accountRating
    val creationDate = component.accountCreationDate
    val picture = component.accountPicture
    val onEvent: (ViewAccountScreenEvent) -> Unit = { component.onEvent(it) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                BoxWithConstraints {
                    val size = min(this.maxWidth, this.maxHeight) / 2
                    DrawIcon(
                        Modifier
                            .size(size)
                            .aspectRatio(1f, true),
                        pictureByteArray = picture,
                        onReload = { onEvent(ViewAccountScreenEvent.ReloadIcon) },
                        onClick = {},
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                }
                DrawName(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = @Composable {
                        Text(
                            it,
                            fontSize = 30.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    accountName = name,
                    onReload = { onEvent(ViewAccountScreenEvent.ReloadName) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
            }
            DrawRating(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(30.dp),
                text = {
                    Text(
                        "${stringResource(Res.string.rating)}: $it",
                        fontSize = 20.sp
                    )
                },
                accountRating = rating,
                reloadRating = { onEvent(ViewAccountScreenEvent.ReloadRating) },
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            DrawAccountCreationDate(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(30.dp),
                text = { (first, second, third) ->
                    Text(
                        "$first-$second-$third",
                        fontSize = 20.sp
                    )
                },
                accountCreationDate = creationDate,
                onReload = {
                    onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                },
                scope = scope, snackbarHostState = snackbarHostState
            )
        }
    }
}
