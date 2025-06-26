package io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.shadowElevation2
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
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
    moveHints: Set<Int>,
    onClick: ((Int) -> Unit)?,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        val heightBigger = derivedStateOf { maxHeight > maxWidth }
        Box(
            modifier = Modifier
                .aspectRatio(1f, !heightBigger.value)
                .clip(RoundedCornerShape3)
                .background(Color(0xFF8F8F8F))
                .padding(15.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DrawGameBoardShadows()
            DrawCircles(pos, selectedButton, moveHints, onClick)
        }
    }
}

/**
 * there are ways not to hard code this, but it looses a lot of readability
 */
@Composable
private fun BoxScope.DrawCircles(
    pos: Position,
    selectedButton: Int?,
    moveHints: Set<Int>,
    onClick: ((Int) -> Unit)?
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
    moveHints: Set<Int>,
    onClick: ((Int) -> Unit)?
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
        range.forEach { index ->
            CircledButton(
                pieceColor = pos.positions[index],
                isSelected = selectedButton == index,
                isHinted = index in moveHints,
                onClick = onClick?.let { { onClick(index) } }
            )
            if (gap > 0 && index != range.last)
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
 * @param pieceColor index of this circle
 * @param onClick function we execute on click
 */
@Composable
fun RowScope.CircledButton(
    pieceColor: Boolean?,
    isSelected: Boolean,
    isHinted: Boolean,
    onClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .wrapContentSize()
    ) {
        AnimatedContent(pieceColor) { pieceColor ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .then(
                        if (pieceColor != null) {
                            Modifier
                                .shadow(shadowElevation2, CircleShape)
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxSize(if (isSelected) 0.7f else 0.9f)
                    .background(Color.Transparent, CircleShape)
                    .semantics {
                        contentDescription =
                            "game piece element with ${
                                when (pieceColor) {
                                    null -> "empty slot"
                                    true -> "white piece"
                                    false -> "black piece"
                                }
                            } value"
                    }
                    .then(
                        if (isHinted)
                            Modifier.border(
                                BorderStroke(
                                    6.dp,
                                    Color.DarkGray
                                ),
                                CircleShape
                            )
                        else
                            Modifier
                    )
                    .background(
                        when (pieceColor) {
                            null -> {
                                Color.Transparent
                            }

                            true -> {
                                ExtendedColorTheme.colorScheme.colorPiece1
                            }

                            false -> {
                                ExtendedColorTheme.colorScheme.colorPiece2
                            }
                        }
                    )
                    .then(
                        if (onClick != null) {
                            Modifier
                                .clickable {
                                    onClick()
                                }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

@Composable
fun RenderUndo(
    handleUndo: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .requiredSize(70.dp),
        onClick = {
            handleUndo()
        },
    ) {
        Icon(
            painter = painterResource(Res.drawable.undo_move), "undo",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun RenderRedo(
    handleRedo: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .requiredSize(70.dp),
        onClick = {
            handleRedo()
        }) {
        Icon(
            painter = painterResource(Res.drawable.redo_move), "redo",
            tint = MaterialTheme.colorScheme.onBackground
        )
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
        RenderUndo(handleUndo)
        RenderRedo(handleRedo)
    }
}
