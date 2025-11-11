package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.runtime.Composable
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_triples_condition_plus_hints
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells how to get removal moves
 */
@Composable
fun RenderTriplesTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  BLUE_,                  BLUE_,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  EMPTY,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                            EMPTY,  GREEN,  EMPTY,
                    EMPTY,          EMPTY,          EMPTY,
            EMPTY,                  BLUE_,                  GREEN
        ),
        // @formatter:on
        0u, 0u, pieceToMove = false, removalCount = 1u
    )
    BaseTutorialScreen(
        position,
        stringResource(Res.string.tutorial_triples_condition_plus_hints),
    )
}
