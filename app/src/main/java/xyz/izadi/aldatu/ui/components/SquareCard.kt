package xyz.izadi.aldatu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SquareCard(
    title: String?,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h5
                )
            }
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 2
                )
            }
        }
    }
}