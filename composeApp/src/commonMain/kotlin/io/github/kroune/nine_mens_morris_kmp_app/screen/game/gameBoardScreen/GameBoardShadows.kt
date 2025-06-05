package io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3

@Composable
fun BoxScope.DrawGameBoardShadows() {
    DrawHorizontalShadows()
    DrawVerticalShadows()
}

@Composable
private fun BoxScope.DrawVerticalShadows() {
    Row(
        modifier = Modifier
            .matchParentSize()
    ) {
        VerticalShadow(0, 7)
        VerticalShadow(1, 5)
        VerticalShadow(2, 3)
        Column(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            with(this@Row) {
                VerticalShadow(0, 3)
                /**
                 * default weight is 1
                 * (1+x+1)/7=x
                 * 2+x=7x
                 * 2=6x
                 * x = 1/3
                 */
                Spacer(modifier = Modifier.weight(1f / 3).fillMaxSize())
                VerticalShadow(0, 3)
            }
        }
        VerticalShadow(2, 3)
        VerticalShadow(1, 5)
        VerticalShadow(0, 7)
    }
}

@Composable
private fun BoxScope.DrawHorizontalShadows() {
    Column(
        modifier = Modifier
            .matchParentSize()
    ) {
        HorizontalShadow(0, 7)
        HorizontalShadow(1, 5)
        HorizontalShadow(2, 3)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            with(this@Column) {
                HorizontalShadow(0, 3)
                /**
                 * default weight is 1
                 * (1+x+1)/7=x
                 * 2+x=7x
                 * 2=6x
                 * x = 1/3
                 */
                Spacer(modifier = Modifier.weight(1f / 3).fillMaxSize())
                HorizontalShadow(0, 3)
            }
        }
        HorizontalShadow(2, 3)
        HorizontalShadow(1, 5)
        HorizontalShadow(0, 7)
    }
}


@Composable
private fun RowScope.VerticalShadow(
    paddingWeight: Int,
    contentWeight: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .weight(contentWeight.toFloat() - 0.5f)
                .alpha(0.5f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
    }
}

@Composable
private fun ColumnScope.HorizontalShadow(
    paddingWeight: Int,
    contentWeight: Int
) {
    Row(
        modifier = Modifier.fillMaxSize().weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .weight(contentWeight.toFloat() - 0.5f)
                .alpha(0.5f)
                .clip(RoundedCornerShape3)
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.fillMaxSize().weight(1f / 4))
        if (paddingWeight > 0)
            Spacer(modifier = Modifier.fillMaxSize().weight(paddingWeight.toFloat()))
    }
}

