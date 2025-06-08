package io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = Modifier
            .shadow(shadowElevation2, CircleShape)
            .size(52.dp * if (shouldMove) 1f else 0.6f)
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
