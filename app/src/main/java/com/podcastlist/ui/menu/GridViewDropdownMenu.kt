package com.podcastlist.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun GridViewDropdownMenu(
    expanded: Boolean,
    cardsPerRow: Float,
    changeCardsPerRow: (Float) -> Unit,
    dismissMenu: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismissMenu,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp, top = 5.dp)
    ) {
        Text(
            text = "Number of podcast cards per row",
            modifier = Modifier.padding(bottom = 7.dp)
        )

        Row(
            modifier = Modifier
                .width(300.dp)
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${floor(cardsPerRow)}")
            Spacer(modifier = Modifier.width(10.dp))
            Slider(
                value = cardsPerRow,
                onValueChange = changeCardsPerRow,
                valueRange = 1f..6f,
                steps = 4
            )
        }
    }
}