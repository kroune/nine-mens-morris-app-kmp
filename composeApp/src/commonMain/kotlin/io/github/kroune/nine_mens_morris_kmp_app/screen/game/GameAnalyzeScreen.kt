package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    startAnalyze: () -> Unit,
    increaseDepth: () -> Unit,
    decreaseDepth: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                startAnalyze()
            },
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
                        modifier = Modifier.weight(1f),
                        onClick = {
                            decreaseDepth()
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
                        modifier = Modifier.weight(1f),
                        onClick = {
                            increaseDepth()
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
        Spacer(modifier = Modifier.height(5.dp))
        if (positions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray, RoundedCornerShape(5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                positions.forEach {
                    RenderGameBoard(
                        modifier = Modifier,
                        pos = it,
                        selectedButton = null,
                        moveHints = mutableListOf(),
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}
