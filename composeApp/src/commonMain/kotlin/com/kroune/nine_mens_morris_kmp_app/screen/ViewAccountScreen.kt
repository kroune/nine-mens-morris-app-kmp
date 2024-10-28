package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.baseline_account_circle_48
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource

@Composable
fun ViewAccountScreen(
    component: ViewAccountScreenComponent
) {
    val isOwnAccount = component.isOwnAccount
    val name = component.accountName
    val rating = component.accountRating
    val creationDate = component.accountCreationDate
    val picture = component.accountPicture
    val onEvent: (ViewAccountScreenEvent) -> Unit = { component.onEvent(it) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            DrawIcon(picture)
            DrawName(name)
        }
        DrawRating(rating)
        DrawAccountCreationDate(creationDate)
        if (isOwnAccount) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                DrawOwnAccountOptions(
                    onEvent
                )
            }
        }
    }
}

/**
 * draws user rating
 */
@Composable
fun DrawRating(accountRating: Long?) {
    if (accountRating == null) {
        Text("User rating is loading...")
    } else {
        Text("User rating is $accountRating")
    }
}

/**
 * draws specific settings for our account
 */
@Composable
fun DrawOwnAccountOptions(
    onEvent: (ViewAccountScreenEvent) -> Unit
) {
    Button(
        onClick = {
            onEvent(ViewAccountScreenEvent.Logout)
        }
    ) {
        Text("Log out")
    }
}

@Composable
fun DrawAccountCreationDate(accountCreationDate: Triple<Int, Int, Int>?) {
    if (accountCreationDate == null) {
        Text("Account information is loading...")
    } else {
        Text("Account was registered at ${accountCreationDate.run { "$first-$second-$third" }}", fontSize = 16.sp)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawIcon(pictureByteArray: ByteArray?) {
    if (pictureByteArray != null) {
        Image(
            bitmap = pictureByteArray.decodeToImageBitmap(),
            contentDescription = "Profile icon",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            painter = painterResource(Res.drawable.baseline_account_circle_48),
            contentDescription = "profile loading icon",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
    }
}

/**
 * draws user name or loading animation
 */
@Composable
fun DrawName(accountName: String?) {
    if (accountName == null) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingCircle()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(accountName, fontSize = 20.sp)
        }
    }
}
