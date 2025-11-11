package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.runtime.Composable
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_fly_plus_hints_condition
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells how to perform flying moves
 */
@Composable
fun RenderFlyingMovesTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  EMPTY,                  EMPTY,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  BLUE_,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  GREEN,
                            EMPTY,  EMPTY,  EMPTY,
                    EMPTY,          EMPTY,          EMPTY,
            EMPTY,                  BLUE_,                  BLUE_
        ),
        // @formatter:on
        0u, 0u,
        pieceToMove = true,
        removalCount = 0u
    )
    BaseTutorialScreen(
        position,
        stringResource(Res.string.tutorial_fly_plus_hints_condition),
    )
}
