package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.model.event.game.GameWithFriendScreenEvent.GameAnalyzeEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.analyze
import ninemensmorrisappkmp.composeapp.generated.resources.depth
import org.jetbrains.compose.resources.stringResource

/**
 * Renders game analysis
 */
@Composable
fun RenderGameAnalyzeScreen(
    modifier: Modifier = Modifier,
    positions: List<Position>,
    depth: Int,
    onEvent: (GameAnalyzeEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(bottom = 5.dp),
            onClick = {
                onEvent(GameAnalyzeEvent.StartAnalyze)
            },
            shape = RoundedCornerShape3,
            colors = ButtonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(Res.string.analyze))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            onEvent(GameAnalyzeEvent.DecreaseAnalyzeDepth)
                        },
                        colors = ButtonColors(
                            containerColor = Color.DarkGray.copy(alpha = 0.2f),
                            contentColor = Color.White,
                            disabledContainerColor = Color.DarkGray.copy(alpha = 0.1f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        // may be it is a bit better to use some icons
                        // but I will leave it like this for now
                        Text("-", fontSize = 30.sp)
                    }
                    Text("${stringResource(Res.string.depth)} - $depth", fontSize = 13.sp)
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = {
                            onEvent(GameAnalyzeEvent.IncreaseAnalyzeDepth)
                        },
                        colors = ButtonColors(
                            containerColor = Color.DarkGray.copy(alpha = 0.2f),
                            contentColor = Color.White,
                            disabledContainerColor = Color.DarkGray.copy(alpha = 0.1f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        // may be it is a bit better to use some icons
                        // but I will leave it like this for now
                        Text("+", fontSize = 22.sp)
                    }
                }
            }
        }
        if (positions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray, RoundedCornerShape(5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(positions) {
                    RenderGameBoard(
                        modifier = Modifier
                            .padding(10.dp),
                        pos = it,
                        selectedButton = null,
                        moveHints = setOf(),
                        onClick = {}
                    )
                }
            }
        }
    }
}
