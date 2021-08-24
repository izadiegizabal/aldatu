package xyz.izadi.aldatu.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun Header(title: String?, subtitle: String?) {
    Spacer(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
    )
    title?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
    subtitle?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        )
    }
}