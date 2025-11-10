package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_indicator_piece
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells about information indicators provide
 */
@Composable
fun RenderIndicatorsTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  BLUE_,                  EMPTY,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  EMPTY,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                            EMPTY,  GREEN,  EMPTY,
                    EMPTY,          BLUE_,          EMPTY,
            EMPTY,                  BLUE_,                  GREEN
        ),
        // @formatter:on
        1u, 2u,
        pieceToMove = false,
        removalCount = 0u,
    )
    Layout(
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
                text = stringResource(Res.string.tutorial_indicator_piece),
                textAlign = TextAlign.Center,
            )
        }
    ) { measurableList, constraints ->
        val leftIndicator = measurableList[0]
        val gameBoard = measurableList[1]
        val rightIndicator = measurableList[2]
        val text = measurableList[3]

        println("constraints - $constraints")
        val textPlaceable = text.measure(constraints)

        val leftIndicatorMinWidth = leftIndicator.minIntrinsicWidth(constraints.maxWidth)
        val rightIndicatorMinWidth = leftIndicator.minIntrinsicWidth(constraints.maxWidth)
        val leftIndicatorMinHeight = leftIndicator.minIntrinsicWidth(constraints.maxWidth)
        val rightIndicatorMinHeight = leftIndicator.minIntrinsicWidth(constraints.maxWidth)

        val gameBoardConstraints = constraints.copy(
            minWidth = constraints.maxWidth - (leftIndicatorMinWidth + rightIndicatorMinWidth),
            maxWidth = constraints.maxWidth - (leftIndicatorMinWidth + rightIndicatorMinWidth),
            minHeight = 0,
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

        println("DEBUG 1 - $leftIndicatorMinWidth to ${leftIndicatorNewPlaceable.width}")
        println("DEBUG 2 - $rightIndicatorMinWidth to ${rightIndicatorNewPlaceable.width}")
        println("DEBUG 3 - ${gameBoardPlaceable.height} to ${gameBoardPlaceable.width}")
        layout(
            constraints.maxWidth,
            constraints.maxHeight,
        ) {
            leftIndicatorNewPlaceable.place(x = 0, y = 0)
            gameBoardPlaceable.place(x = leftIndicatorMinWidth, y = 0)
            rightIndicatorNewPlaceable.place(x = constraints.maxWidth - rightIndicatorMinWidth, y = 0)

            textPlaceable.place(x = 0, y = upPartHeight)
        }

    }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Top
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(IntrinsicSize.Min)
//        ) {
//        }
//    }
}
