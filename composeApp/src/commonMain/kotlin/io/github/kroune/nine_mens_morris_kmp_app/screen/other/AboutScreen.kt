package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
                    {
                        onEvent(AboutScreenEvent.OnBackPressed)
                    }
                ) {
                    Icon(
                        painterResource(Res.drawable.close),
                        "close",
                        Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(padding2)
        ) {
            val uriHandler = LocalUriHandler.current
            Button(
                modifier = Modifier
                    .padding(horizontal = padding2),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    onEvent(AboutScreenEvent.OnNavigationToReportAnIssue)
                    uriHandler.openUri(state.githubIssue.webLink)
                },
                shape = RoundedCornerShape3,
                elevation = ButtonDefaults
                    .buttonElevation(shadowElevation2),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(padding2)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(padding2)
                ) {
                    Icon(
                        painterResource(Res.drawable.report),
                        "github",
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Text(stringResource(Res.string.report_an_issue))
                }
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = padding2),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape3,
                elevation = ButtonDefaults
                    .elevatedButtonElevation(shadowElevation2),
                onClick = {
                    onEvent(AboutScreenEvent.OnNavigationToSourceCode)
                    uriHandler.openUri(state.github.webLink)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(padding2)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(padding2)
                ) {
                    Icon(
                        painterResource(Res.drawable.github),
                        "github",
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Text(stringResource(Res.string.source_code_link))
                }
            }
            Button(
                modifier = Modifier
                    .padding(horizontal = padding2),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape3,
                elevation = ButtonDefaults.buttonElevation(shadowElevation2),
                onClick = {
                    onEvent(AboutScreenEvent.OnNavigationToCreatorTelegram)
                    uriHandler.openUri(state.telegram.webLink)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(padding2)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(padding2)
                ) {
                    Icon(
                        painterResource(Res.drawable.telegram),
                        "telegram",
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Text(stringResource(Res.string.creator_telegram_link))
                }
            }
        }
    }
}