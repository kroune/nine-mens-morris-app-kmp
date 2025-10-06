package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.aboutScreenComponent.AboutScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.AboutScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.padding2
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.shadowElevation2
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.about_app
import ninemensmorrisappkmp.composeapp.generated.resources.close
import ninemensmorrisappkmp.composeapp.generated.resources.creator_telegram_link
import ninemensmorrisappkmp.composeapp.generated.resources.github
import ninemensmorrisappkmp.composeapp.generated.resources.report
import ninemensmorrisappkmp.composeapp.generated.resources.report_an_issue
import ninemensmorrisappkmp.composeapp.generated.resources.source_code_link
import ninemensmorrisappkmp.composeapp.generated.resources.telegram
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutScreen(
    onEvent: (AboutScreenEvent) -> Unit,
    state: AboutScreenState
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(padding2)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.about_app),
                    fontWeight = FontWeight.W500,
                    fontSize = 23.sp
                )
                IconButton(
                    { onEvent(AboutScreenEvent.OnBackPressed) }
                ) {
                    Icon(
                        painterResource(Res.drawable.close),
                        "close",
                        Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(padding2)
        ) {
            DrawElement(
                DrawElementInfo(
                    Res.drawable.report,
                    "github",
                    Res.string.report_an_issue,
                    state.githubIssue.webLink,
                ),
            ) {
                onEvent(AboutScreenEvent.OnNavigationToReportAnIssue)
            }
            DrawElement(
                DrawElementInfo(
                    Res.drawable.github,
                    "github",
                    Res.string.source_code_link,
                    state.github.webLink,
                ),
            ) {
                onEvent(AboutScreenEvent.OnNavigationToSourceCode)
            }
            DrawElement(
                DrawElementInfo(
                    Res.drawable.telegram,
                    "telegram",
                    Res.string.creator_telegram_link,
                    state.telegram.webLink,
                ),
            ) {
                onEvent(AboutScreenEvent.OnNavigationToCreatorTelegram)
            }
        }
    }
}

private data class DrawElementInfo(
    val painterResource: DrawableResource,
    val painterResourceDescription: String,
    val textResource: StringResource,
    val uri: String
)

@Composable
private fun DrawElement(
    drawElementInfo: DrawElementInfo,
    onEvent: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    Surface(
        modifier = Modifier
            .padding(horizontal = padding2)
            .fillMaxWidth()
            .clickable {
                onEvent()
                uriHandler.openUri(drawElementInfo.uri)
            },
        shape = RoundedCornerShape3,
        shadowElevation = shadowElevation2
    ) {
        Row(
            modifier = Modifier.padding(padding2),
            horizontalArrangement = Arrangement.spacedBy(padding2)
        ) {
            Icon(
                painter = painterResource(drawElementInfo.painterResource),
                contentDescription = drawElementInfo.painterResourceDescription,
                modifier = Modifier.size(24.dp)
            )
            Text(stringResource(drawElementInfo.textResource))
        }
    }
}