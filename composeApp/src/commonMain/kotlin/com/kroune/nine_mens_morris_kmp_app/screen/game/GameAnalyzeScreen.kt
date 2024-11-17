package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH

/**
 * Renders game analysis
 */
@Composable
fun RenderGameAnalyzeScreen(
    positions: List<Position>,
    depth: Int,
    startAnalyze: () -> Unit,
    increaseDepth: () -> Unit,
    decreaseDepth: () -> Unit
) {
    // TODO: decide what to do when there isn't enough space for analyze screen
    if (positions.isNotEmpty()) {
        Box(
            modifier = Modifier
                .padding(0.dp, GAME_BOARD_BUTTON_WIDTH * 3f, 0.dp, 0.dp)
                .background(Color.DarkGray, RoundedCornerShape(5))
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .weight(1f, false)
                ) {
                    positions.forEach {
                        RenderGameBoard(
                            it,
                            null,
                            mutableListOf(),
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(), Alignment.TopCenter
    ) {
        Button(onClick = {
            startAnalyze()
        }) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Analyze")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            decreaseDepth()
                        },
                        colors = object : ButtonColors {
                            @Composable
                            override fun backgroundColor(enabled: Boolean): State<Color> {
                                return mutableStateOf(Color(177, 134, 255, 50))
                            }

                            @Composable
                            override fun contentColor(enabled: Boolean): State<Color> {
                                return mutableStateOf(Color.White)
                            }
                        }
                    ) {
                        // may be it is a bit better to use some icons
                        // but I will leave it like this for now
                        Text("-", fontSize = 30.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("depth - $depth", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            increaseDepth()
                        },
                        colors = object : ButtonColors {
                            @Composable
                            override fun backgroundColor(enabled: Boolean): State<Color> {
                                return mutableStateOf(Color(177, 134, 255, 50))
                            }

                            @Composable
                            override fun contentColor(enabled: Boolean): State<Color> {
                                return mutableStateOf(Color.White)
                            }
                        }
                    ) {
                        // may be it is a bit better to use some icons
                        // but I will leave it like this for now
                        Text("+", fontSize = 22.sp)
                    }
                }
            }
        }
    }
}
