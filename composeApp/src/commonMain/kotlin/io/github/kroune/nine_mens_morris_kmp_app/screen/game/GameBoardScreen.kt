package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.redo_move
import ninemensmorrisappkmp.composeapp.generated.resources.undo_move
import org.jetbrains.compose.resources.painterResource

/**
 * renders game board
 */
@Composable
fun RenderGameBoard(
    modifier: Modifier = Modifier,
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit,
) {
    BoxWithConstraints(contentAlignment = Alignment.TopCenter) {
        val heightBigger = derivedStateOf { maxHeight > maxWidth }
        Box(
            modifier = modifier
                .then(
                    if (!heightBigger.value)
                        Modifier.fillMaxHeight()
                    else
                        Modifier.fillMaxWidth()
                )
                .aspectRatio(1f, !heightBigger.value)
                .clip(RoundedCornerShape(15))
                .background(Color(0xFF8F8F8F))
                .padding(15.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DrawHorizontalShadows()
            DrawVerticalShadows()
            DrawCircles(pos, selectedButton, moveHints, onClick)
        }
    }
}

/**
 * renders piece counters
 */
@Composable
fun RenderPieceCount(pos: Position) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RenderPieceCountElement(true, pos.pieceToMove, pos.freeGreenPieces)
        RenderPieceCountElement(false, !pos.pieceToMove, pos.freeBluePieces)
    }
}

@Composable
fun RenderPieceCountElement(
    isGreen: Boolean,
    shouldMove: Boolean,
    freePieces: UByte
) {
    val backgroundColor = if (isGreen) Color.Black else Color.White
    val textColor = if (!isGreen) Color.Black else Color.White
    Box(
        modifier = Modifier
            .size(GAME_BOARD_BUTTON_WIDTH * 1.5f * if (shouldMove) 1f else 0.6f)
            .alpha(if (shouldMove) 1f else 0.6f)
            .background(backgroundColor, CircleShape),
        Alignment.Center
    ) {
        if (freePieces != 0.toUByte())
            Text(
                color = textColor,
                text = freePieces.toString()
            )
    }
}

/**
 * there are ways not to hard code this, but it looses a lot of readability
 */
@Composable
private fun BoxScope.DrawCircles(
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .matchParentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RowOfCircles(0, 2, 0..2, pos, selectedButton, moveHints, onClick)
        RowOfCircles(1, 1, 3..5, pos, selectedButton, moveHints, onClick)
        RowOfCircles(2, 0, 6..8, pos, selectedButton, moveHints, onClick)
        Row(
            modifier = Modifier.weight(1f).fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            with(this@Column) {
                RowOfCircles(0, 0, 9..11, pos, selectedButton, moveHints, onClick)
                /**
                 * default weight is 1
                 * (1+x+1)/7=x
                 * 2+x=7x
                 * 2=6x
                 * x = 1/3
                 */
                Spacer(modifier = Modifier.weight(1f / 3).fillMaxSize())
                RowOfCircles(0, 0, 12..14, pos, selectedButton, moveHints, onClick)
            }
        }
        RowOfCircles(2, 0, 15..17, pos, selectedButton, moveHints, onClick)
        RowOfCircles(1, 1, 18..20, pos, selectedButton, moveHints, onClick)
        RowOfCircles(0, 2, 21..23, pos, selectedButton, moveHints, onClick)
    }
}

@Composable
private fun BoxScope.DrawVerticalShadows() {
    Row(
        modifier = Modifier
            .matchParentSize()
    ) {
        VerticalShadow(0, 7)
        VerticalShadow(1, 5)
        VerticalShadow(2, 3)
        Column(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            with(this@Row) {
                VerticalShadow(0, 3)
                /**
                 * default weight is 1
                 * (1+x+1)/7=x
                 * 2+x=7x
                 * 2=6x
                 * x = 1/3
                 */
                Spacer(modifier = Modifier.weight(1f / 3).fillMaxSize())
                VerticalShadow(0, 3)
            }
        }
        VerticalShadow(2, 3)
        VerticalShadow(1, 5)
        VerticalShadow(0, 7)
    }
}

@Composable
private fun BoxScope.DrawHorizontalShadows() {
    Column(
        modifier = Modifier
            .matchParentSize()
    ) {
        HorizontalShadow(0, 7)
        HorizontalShadow(1, 5)
        HorizontalShadow(2, 3)
        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            with(this@Column) {
                HorizontalShadow(0, 3)
                /**
                 * default weight is 1
                 * (1+x+1)/7=x
                 * 2+x=7x
                 * 2=6x
                 * x = 1/3
                 */
                Spacer(modifier = Modifier.weight(1f / 3).fillMaxSize())
                HorizontalShadow(0, 3)
            }
        }
        HorizontalShadow(2, 3)
        HorizontalShadow(1, 5)
        HorizontalShadow(0, 7)
    }
}

@Composable
private fun RowScope.VerticalShadow(
    paddingWeight: Int,
    contentWeight: Int
) {
    Column(
        modifier = Modifier.fillMaxSize().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .weight(contentWeight.toFloat() - 0.5f)
                .alpha(0.5f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
    }
}

@Composable
private fun ColumnScope.HorizontalShadow(
    paddingWeight: Int,
    contentWeight: Int
) {
    Row(
        modifier = Modifier.fillMaxSize().weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .weight(contentWeight.toFloat() - 0.5f)
                .alpha(0.5f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
    }
}

/**
 * draws a raw of circles
 * @param padding padding
 * @param gap between elements
 * @param range range of indexes
 */
@Composable
private fun ColumnScope.RowOfCircles(
    padding: Int,
    gap: Int,
    range: IntRange,
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (padding > 0)
            Spacer(
                modifier = Modifier
                    .fillMaxSize().weight(padding.toFloat())
            )
        range.forEach { i ->
            CircledButton(
                elementIndex = i,
                pos = pos,
                selectedButton = selectedButton,
                moveHints = moveHints
            ) {
                onClick(i)
            }
            if (gap > 0 && i != range.last)
                Spacer(
                    modifier = Modifier
                        .fillMaxSize().weight(gap.toFloat())
                )
        }
        if (padding > 0) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize().weight(padding.toFloat())
            )
        }
    }
}

/**
 * draws a circles button
 * @param elementIndex index of this circle
 * @param onClick function we execute on click
 */
@Composable
fun RowScope.CircledButton(
    elementIndex: Int,
    pos: Position,
    selectedButton: Int?,
    moveHints: List<Int>,
    onClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().weight(1f).wrapContentSize()) {
        Button(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxSize(if (selectedButton == elementIndex) 0.7f else 0.9f)
                .background(Color.Transparent)
                .semantics {
                    contentDescription =
                        "game piece element with ${pos.positions[elementIndex]} value"
                }
                .border(
                    if (!moveHints.contains(elementIndex)) BorderStroke(
                        0.dp,
                        Color.Transparent
                    ) else BorderStroke(
                        5.dp,
                        Color.DarkGray
                    ), CircleShape
                ),
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (pos.positions[elementIndex]) {
                    null -> {
                        Color.Transparent
                    }

                    true -> {
                        Color.Black
                    }

                    false -> {
                        Color.White
                    }
                },
                disabledContainerColor = Color.Transparent
            ),
            onClick = {
                onClick(elementIndex)
            }
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
            .zIndex(2f)
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
                painter = painterResource(Res.drawable.undo_move), "undo",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(
            modifier = Modifier
                .size(GAME_BOARD_BUTTON_WIDTH * 2f),
            onClick = {
                handleRedo()
            }) {
            Icon(
                painter = painterResource(Res.drawable.redo_move), "redo",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
