import androidx.compose.runtime.mutableStateOf
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.PastGamesUiModel
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.account.viewOwnAccountScreen.ViewOwnAccountScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val viewOwnAccount by story {
    AppTheme {
        ViewOwnAccountScreen(
            ViewOwnAccountScreenState(
                null,
                null,
                null,
                null,
                null,
                1,
                PastGamesApiResponse.Success(
                    listOf(
                        PastGamesUiModel(
                            1,
                            12,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            13,
                            11,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            -13,
                            10020,
                            51
                        ),
                        PastGamesUiModel(
                            1,
                            12,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            13,
                            11,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            -13,
                            10020,
                            51
                        ),
                        PastGamesUiModel(
                            1,
                            12,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            13,
                            11,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            -13,
                            10020,
                            51
                        ),
                        PastGamesUiModel(
                            1,
                            12,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            13,
                            11,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            -13,
                            10020,
                            51
                        ),
                        PastGamesUiModel(
                            1,
                            12,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            13,
                            11,
                            mutableStateOf(null),
                            mutableStateOf(null),
                            -13,
                            10020,
                            51
                        ),
                    )
                ),
                false,
            )
        ) {}
    }
}