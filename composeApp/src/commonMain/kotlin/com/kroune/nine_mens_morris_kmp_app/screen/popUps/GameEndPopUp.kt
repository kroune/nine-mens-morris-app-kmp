package com.kroune.nine_mens_morris_kmp_app.screen.popUps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_ended
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameEndPopUp(
    onClick: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Title") },
            text = { Text("This area typically contains the supportive text " + "which presents the details regarding the Dialog's purpose.") },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            openDialog.value = false
                            onClick()
                        }) {
                        Text("Go to main screen")
                    }
                }
            })
    }
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .zIndex(2f),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(
//            onClick = {
//                onClick()
//            },
//            modifier = Modifier
//                .fillMaxHeight(0.2f)
//                .fillMaxWidth(0.2f)
//                .align(Alignment.Center)
//                .background(Color.Red.copy(alpha = 0.5f), RoundedCornerShape(10)),
//        ) {
//            Text(stringResource(Res.string.game_ended))
//        }
//    }
}