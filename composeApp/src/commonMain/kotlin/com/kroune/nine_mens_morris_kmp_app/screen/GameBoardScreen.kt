package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.redo_move
import ninemensmorrisappkmp.composeapp.generated.resources.undo_move
import org.jetbrains.compose.resources.painterResource

/**
 * renders game board
 */
@Composable
fun RenderGameBoard(
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(), Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(GAME_BOARD_BUTTON_WIDTH)
                .clip(RoundedCornerShape(15))
                .size(GAME_BOARD_BUTTON_WIDTH * 8.25f)
                .background(Color(0xFF8F8F8F)),
            Alignment.Center,
        ) {
            DrawCircles(pos, selectedButton, moveHints, onClick)
            DrawHorizontalShadows()
            DrawVerticalShadows()
        }
    }
}

/**
 * renders piece counters
 */
@Composable
fun RenderPieceCount(pos: Position) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .size(GAME_BOARD_BUTTON_WIDTH * if (pos.pieceToMove) 1.5f else 1f)
                    .background(Color.Green, CircleShape)
                    .alpha(if (pos.freeGreenPieces == 0.toUByte()) 0f else 1f),
                Alignment.Center
            ) {
                Text(
                    color = Color.Blue,
                    text = pos.freeGreenPieces.toString()
                )
            }
        }
        Box(
            modifier = Modifier, contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .size(GAME_BOARD_BUTTON_WIDTH * if (!pos.pieceToMove) 1.5f else 1f)
                    .background(Color.Blue, CircleShape)
                    .alpha(if (pos.freeBluePieces == 0.toUByte()) 0f else 1f),
                Alignment.Center
            ) {
                Text(
                    color = Color.Green,
                    text = pos.freeBluePieces.toString()
                )
            }
        }
    }
}

/**
 * there are ways not to hard code this, but it looses a lot of readability
 */
@Composable
private fun DrawCircles(
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.size(GAME_BOARD_BUTTON_WIDTH * 7, GAME_BOARD_BUTTON_WIDTH * 7)
    ) {
        RowOfCircles(0, 2, 0..2, pos, selectedButton, moveHints, onClick)
        RowOfCircles(1, 1, 3..5, pos, selectedButton, moveHints, onClick)
        RowOfCircles(2, 0, 6..8, pos, selectedButton, moveHints, onClick)
        Box {
            RowOfCircles(0, 0, 9..11, pos, selectedButton, moveHints, onClick)
            RowOfCircles(4, 0, 12..14, pos, selectedButton, moveHints, onClick)
        }
        RowOfCircles(2, 0, 15..17, pos, selectedButton, moveHints, onClick)
        RowOfCircles(1, 1, 18..20, pos, selectedButton, moveHints, onClick)
        RowOfCircles(0, 2, 21..23, pos, selectedButton, moveHints, onClick)
    }
}

@Composable
private fun DrawVerticalShadows() {
    Row(
        modifier = Modifier.size(GAME_BOARD_BUTTON_WIDTH * 7, GAME_BOARD_BUTTON_WIDTH * 7)
    ) {
        VerticalShadow(0.25f, 0.25f)
        VerticalShadow(1.25f, 1.25f)
        VerticalShadow(2.25f, 2.25f)
        Column(
            modifier = Modifier
                .padding(
                    top = GAME_BOARD_BUTTON_WIDTH * 0.25f, bottom = GAME_BOARD_BUTTON_WIDTH * 0.25f
                )
                .height(GAME_BOARD_BUTTON_WIDTH * 7)
                .width(GAME_BOARD_BUTTON_WIDTH),
            verticalArrangement = Arrangement.spacedBy(
                GAME_BOARD_BUTTON_WIDTH * 1.5f, Alignment.CenterVertically
            )
        ) {
            VerticalShadow(0f, 0f, 2.5f, 2.5f)
            VerticalShadow(0f, 0f, 2.5f, 2.5f)
        }
        VerticalShadow(2.25f, 2.25f)
        VerticalShadow(1.25f, 1.25f)
        VerticalShadow(0.25f, 0.25f)
    }
}

@Composable
private fun DrawHorizontalShadows() {
    Column(
        modifier = Modifier.size(GAME_BOARD_BUTTON_WIDTH * 7, GAME_BOARD_BUTTON_WIDTH * 7)
    ) {
        HorizontalShadow(0.25f, 0.25f)
        HorizontalShadow(1.25f, 1.25f)
        HorizontalShadow(2.25f, 2.25f)
        Row(
            modifier = Modifier
                .padding(
                    start = GAME_BOARD_BUTTON_WIDTH * 0.25f, end = GAME_BOARD_BUTTON_WIDTH * 0.25f
                )
                .height(GAME_BOARD_BUTTON_WIDTH)
                .width(GAME_BOARD_BUTTON_WIDTH * 7),
            horizontalArrangement = Arrangement.spacedBy(
                GAME_BOARD_BUTTON_WIDTH * 1.5f, Alignment.CenterHorizontally
            )
        ) {
            HorizontalShadow(0f, 0f, 2.5f, 2.5f)
            HorizontalShadow(0f, 0f, 2.5f, 2.5f)
        }
        HorizontalShadow(2.25f, 2.25f)
        HorizontalShadow(1.25f, 1.25f)
        HorizontalShadow(0.25f, 0.25f)
    }
}

@Composable
private fun VerticalShadow(
    paddingLeft: Float,
    paddingRight: Float,
    length1: Float = 7f,
    length2: Float = (7 - (paddingLeft + paddingRight))
) {
    Box(
        modifier = Modifier
            .height(GAME_BOARD_BUTTON_WIDTH * length1)
            .width(GAME_BOARD_BUTTON_WIDTH * 1f)
            .padding(
                top = GAME_BOARD_BUTTON_WIDTH * paddingLeft,
                start = GAME_BOARD_BUTTON_WIDTH * 0.25f,
                end = GAME_BOARD_BUTTON_WIDTH * 0.25f,
                bottom = GAME_BOARD_BUTTON_WIDTH * paddingRight
            )
    ) {
        Box(
            modifier = Modifier
                .alpha(0.5f)
                .height(GAME_BOARD_BUTTON_WIDTH * length2)
                .width(GAME_BOARD_BUTTON_WIDTH * 0.5f)
                .shadow(
                    elevation = 10.dp, shape = RoundedCornerShape(8.dp)
                )
                .background(Color.DarkGray)
        ) {}
    }
}

@Composable
private fun HorizontalShadow(
    paddingLeft: Float,
    paddingRight: Float,
    length1: Float = 7f,
    length2: Float = (7 - (paddingLeft + paddingRight))
) {
    Box(
        modifier = Modifier
            .height(GAME_BOARD_BUTTON_WIDTH * 1f)
            .width(GAME_BOARD_BUTTON_WIDTH * length1)
            .padding(
                top = GAME_BOARD_BUTTON_WIDTH * 0.25f,
                start = GAME_BOARD_BUTTON_WIDTH * paddingLeft,
                end = GAME_BOARD_BUTTON_WIDTH * paddingRight,
                bottom = GAME_BOARD_BUTTON_WIDTH * 0.25f
            )
    ) {
        Box(
            modifier = Modifier
                .alpha(0.5f)
                .height(GAME_BOARD_BUTTON_WIDTH * 0.5f)
                .width(GAME_BOARD_BUTTON_WIDTH * length2)
                .shadow(
                    elevation = 10.dp, shape = RoundedCornerShape(8.dp)
                )
                .background(Color.DarkGray)
        ) {}
    }
}

/**
 * draws a raw of circles
 * @param padding padding
 * @param gap between elements
 * @param range range of indexes
 */
@Composable
private fun RowOfCircles(
    padding: Int, gap: Int, range: IntRange,
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.padding(start = GAME_BOARD_BUTTON_WIDTH * padding),
        horizontalArrangement = Arrangement.spacedBy(
            GAME_BOARD_BUTTON_WIDTH * gap, Alignment.CenterHorizontally
        ),
        Alignment.CenterVertically,
    ) {
        for (i in range) {
            CircledButton(
                elementIndex = i,
                pos = pos,
                selectedButton = selectedButton,
                moveHints = moveHints
            ) {
                onClick(i)
            }
        }
    }
}

/**
 * draws a circles button
 * @param elementIndex index of this circle
 * @param onClick function we execute on click
 */
@Composable
private fun CircledButton(
    elementIndex: Int,
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(GAME_BOARD_BUTTON_WIDTH),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .size(GAME_BOARD_BUTTON_WIDTH * if (selectedButton == elementIndex) 0.8f else 1f)
                .background(Color.Transparent, CircleShape),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = when (pos.positions[elementIndex]) {
                    null -> {
                        Color.Transparent
                    }

                    true -> {
                        Color.Green
                    }

                    false -> {
                        Color.Blue
                    }
                }
            ),
            shape = CircleShape,
            onClick = {
                onClick(elementIndex)
            },
            border = if (!moveHints.contains(elementIndex)) null else BorderStroke(
                GAME_BOARD_BUTTON_WIDTH * 0.1f,
                Color.DarkGray
            )
        ) {}
    }
}

/**
 * renders undo buttons
 */
@Composable
fun RenderUndoRedo(handleUndo: () -> Unit, handleRedo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            modifier = Modifier
                .size(GAME_BOARD_BUTTON_WIDTH * 2f),
            onClick = {
                handleUndo()
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.redo_move), "undo"
            )
        }
        IconButton(modifier = Modifier
            .size(GAME_BOARD_BUTTON_WIDTH * 2f),
            onClick = {
                handleRedo()
            }) {
            Icon(
                painter = painterResource(Res.drawable.undo_move), "redo"
            )
        }
    }
}
