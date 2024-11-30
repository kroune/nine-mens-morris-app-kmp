package com.kroune.nine_mens_morris_kmp_app.screen.popUps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.BlackGrayColors
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.back_to_main_screen
import ninemensmorrisappkmp.composeapp.generated.resources.game_ended
import ninemensmorrisappkmp.composeapp.generated.resources.keep_me_here
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameEndPopUp(
    onDismiss: () -> Unit,
    onDiscarded: () -> Unit,
    onBackToMainScreen: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.5f))
            .border(1.dp, Color.White),
        onDismissRequest = { onDismiss() },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.game_ended))
            }
        },
        buttons = {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = BlackGrayColors(),
                    onClick = { onDiscarded() }
                ) {
                    Text(stringResource(Res.string.keep_me_here))
                }
                Spacer(modifier = Modifier.width(15.dp))
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = BlackGrayColors(),
                    onClick = {
                        onBackToMainScreen()
                    }
                ) {
                    Text(stringResource(Res.string.back_to_main_screen))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        },
        backgroundColor = Color.DarkGray
    )
}