package io.github.kroune.nine_mens_morris_kmp_app.screen.popUps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.back_to_main_screen
import ninemensmorrisappkmp.composeapp.generated.resources.game_ended
import ninemensmorrisappkmp.composeapp.generated.resources.keep_me_here
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameEndPopUp(
    onDismiss: () -> Unit,
    onDiscarded: () -> Unit,
    onBackToMainScreen: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .clip(RoundedCornerShape(10))
            .background(Color.Gray.copy(alpha = 0.5f))
            .border(1.dp, Color.White, RoundedCornerShape(10)),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                modifier = Modifier.padding(UiConstants.padding2),
                onClick = { onBackToMainScreen() },
            ) {
                Text(stringResource(Res.string.back_to_main_screen))
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier.padding(UiConstants.padding2),
                onClick = { onDiscarded() },
            ) {
                Text(stringResource(Res.string.keep_me_here))
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.game_ended))
            }
        },
        containerColor = Color.DarkGray,
    )
}