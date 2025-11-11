package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement

@Composable
fun BaseTutorialScreen(
    position: Position,
    textString: String,
) {
    Layout(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing),
        content = {
            RenderPieceCountElement(
                true,
                position.pieceToMove,
                position.freeGreenPieces,
            )
            RenderGameBoard(
                pos = position,
                selectedButton = 3,
                moveHints = setOf(),
                onClick = null,
            )
            RenderPieceCountElement(
                false,
                !position.pieceToMove,
                position.freeBluePieces,
            )
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                text = textString,
                textAlign = TextAlign.Center,
            )
        }
    ) { measurableList, constraints ->
        val leftIndicator = measurableList[0]
        val gameBoard = measurableList[1]
        val rightIndicator = measurableList[2]

        val text = measurableList[3]
        val textPlaceable = text.measure(constraints)

        val leftIndicatorMinWidth = leftIndicator.minIntrinsicWidth(constraints.maxWidth)
        val leftIndicatorMinHeight = leftIndicator.minIntrinsicWidth(constraints.maxWidth)
        val rightIndicatorMinWidth = rightIndicator.minIntrinsicWidth(constraints.maxWidth)
        val rightIndicatorMinHeight = rightIndicator.minIntrinsicWidth(constraints.maxWidth)

        val gameBoardConstraints = constraints.copy(
            minWidth = constraints.maxWidth - (leftIndicatorMinWidth + rightIndicatorMinWidth),
            maxWidth = constraints.maxWidth - (leftIndicatorMinWidth + rightIndicatorMinWidth),
            maxHeight = constraints.maxHeight - text.minIntrinsicHeight(constraints.maxWidth),
        )
        val gameBoardPlaceable = gameBoard.measure(gameBoardConstraints)

        val upPartHeight = maxOf(
            gameBoardPlaceable.height,
            leftIndicatorMinHeight,
            rightIndicatorMinHeight,
        )
        val newConstraints = constraints.copy(
            minHeight = upPartHeight,
            maxHeight = upPartHeight,
        )
        val leftIndicatorNewPlaceable = leftIndicator.measure(newConstraints)
        val rightIndicatorNewPlaceable = rightIndicator.measure(newConstraints)
        layout(
            constraints.maxWidth,
            constraints.maxHeight,
        ) {
            leftIndicatorNewPlaceable.place(x = 0, y = 0)
            gameBoardPlaceable.place(x = leftIndicatorMinWidth, y = 0)
            rightIndicatorNewPlaceable.place(
                x = constraints.maxWidth - rightIndicatorMinWidth,
                y = 0
            )

            textPlaceable.place(
                x = (constraints.maxWidth - textPlaceable.width) / 2,
                y = upPartHeight
            )
        }
    }
}