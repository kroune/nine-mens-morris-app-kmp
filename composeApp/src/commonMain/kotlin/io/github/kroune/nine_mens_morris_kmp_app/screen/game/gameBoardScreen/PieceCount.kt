package io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.shadowElevation2
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme

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
    val backgroundColor =
        if (isGreen) ExtendedColorTheme.colorScheme.colorPiece1 else ExtendedColorTheme.colorScheme.colorPiece2
    val textColor =
        if (!isGreen) ExtendedColorTheme.colorScheme.colorPiece1 else ExtendedColorTheme.colorScheme.colorPiece2
    val borderColor =
        if (!isGreen) ExtendedColorTheme.colorScheme.colorPiece1 else ExtendedColorTheme.colorScheme.colorPiece2
    val animatedAlpha by animateFloatAsState(
        targetValue = if (shouldMove) 1f else 0.5f,
        label = "alpha"
    )
    val animatedOffset by animateDpAsState(
        targetValue = if (shouldMove) 20.dp else 15.dp
    )
    val padding = 10.dp
    val offset = animatedOffset
    Box(
        modifier = Modifier
            .width(40.dp + padding * 2)
            .height(40.dp * freePieces.toInt() - offset * (freePieces.toInt() - 1) + padding * 2)
            .alpha(animatedAlpha),
        contentAlignment = Alignment.TopCenter
    ) {
        repeat(freePieces.toInt()) {
            Box(
                modifier = Modifier
                    .offset(y = padding + offset * it)
                    .zIndex(freePieces.toInt() - it.toFloat())
                    .shadow(
                        shadowElevation2,
                        CircleShape
                    )
                    .border(1.dp, borderColor, CircleShape)
                    .clip(CircleShape)
                    .background(backgroundColor, CircleShape)
                    .size(40.dp),
                Alignment.Center
            ) {
                if (it == 0)
                    Text(
                        color = textColor,
                        text = freePieces.toString()
                    )
            }
        }
    }
}
