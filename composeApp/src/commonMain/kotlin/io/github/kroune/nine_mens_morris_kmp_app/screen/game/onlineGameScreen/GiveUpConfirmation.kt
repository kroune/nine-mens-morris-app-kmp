package io.github.kroune.nine_mens_morris_kmp_app.screen.game.onlineGameScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import io.ktor.websocket.Frame.Text
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.no
import ninemensmorrisappkmp.composeapp.generated.resources.want_to_give_up
import ninemensmorrisappkmp.composeapp.generated.resources.yes
import org.jetbrains.compose.resources.stringResource

@Composable
fun GiveUpConfirmation(
    onGiveUpDiscarded: () -> Unit,
    onGiveUp: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onGiveUpDiscarded()
        },
        title = {
            Text(stringResource(Res.string.want_to_give_up))
        },
        confirmButton = {
            Button(
                onClick = {
                    onGiveUp()
                }
            ) {
                Text(stringResource(Res.string.yes))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onGiveUpDiscarded()
                }
            ) {
                Text(stringResource(Res.string.no))
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}
