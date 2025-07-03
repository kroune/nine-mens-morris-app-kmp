package io.github.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game.GameWithFriendScreenEvent.GameAnalyzeEvent
import io.github.kroune.nine_mens_morris_kmp_app.getScreenDpSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCountElement
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.analyze
import ninemensmorrisappkmp.composeapp.generated.resources.depth
import ninemensmorrisappkmp.composeapp.generated.resources.minus
import ninemensmorrisappkmp.composeapp.generated.resources.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Renders game analysis
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenderGameAnalyzeScreen(
    modifier: Modifier = Modifier,
    positions: List<Position>,
    depth: Int,
    onEvent: (GameAnalyzeEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .padding(bottom = 5.dp),
            onClick = {
                onEvent(GameAnalyzeEvent.StartAnalyze)
            },
            shape = RoundedCornerShape3,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(Res.string.analyze))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.minus),
                            "minus",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onEvent(GameAnalyzeEvent.DecreaseAnalyzeDepth)
                                }
                        )
                    }
                    Text("${stringResource(Res.string.depth)} - $depth", fontSize = 13.sp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.plus),
                            "plus",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onEvent(GameAnalyzeEvent.IncreaseAnalyzeDepth)
                                }
                        )
                    }
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    if (positions.isNotEmpty()) {
        val bottomSheet =
            rememberStandardBottomSheetState(SheetValue.Hidden, skipHiddenState = false)
        val (screenWidth, screenHeight) = getScreenDpSize()
        val minSide = min(screenWidth, screenHeight)
        ModalBottomSheet(
            {
                scope.launch {
                    bottomSheet.hide()
                    onEvent(GameAnalyzeEvent.CloseAnalyze)
                }
            },
            modifier = Modifier
                .width(minSide)
                .padding(horizontal = 20.dp),
            sheetState = bottomSheet
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(positions) {
                    Row {
                        RenderPieceCountElement(
                            true,
                            it.pieceToMove,
                            it.freeGreenPieces
                        )
                        RenderGameBoard(
                            modifier = Modifier
                                .weight(1f)
                                .padding(10.dp),
                            pos = it,
                            selectedButton = null,
                            moveHints = setOf(),
                            onClick = null
                        )
                        RenderPieceCountElement(
                            false,
                            !it.pieceToMove,
                            it.freeBluePieces
                        )
                    }
                }
            }
        }
    }
}
