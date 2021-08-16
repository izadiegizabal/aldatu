package xyz.izadi.aldatu.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import xyz.izadi.aldatu.R

@Composable
fun Header(refreshDate: String?) {
    Spacer(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.h2,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    refreshDate?.let {
        Text(
            text = stringResource(R.string.ms_refresh_time, it),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        )
    }
}