package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.runtime.Composable
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_placement_condition_plus_hints
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells how to perform placements
 */
@Composable
fun RenderPlacementTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  BLUE_,                  GREEN,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  EMPTY,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  BLUE_,
                            EMPTY,  GREEN,  EMPTY,
                    EMPTY,          BLUE_,          EMPTY,
            EMPTY,                  BLUE_,                  GREEN
        ),
        // @formatter:on
        1u, 2u, pieceToMove = false, removalCount = 0u
    )
    BaseTutorialScreen(
        position,
        stringResource(Res.string.tutorial_placement_condition_plus_hints),
    )
}
