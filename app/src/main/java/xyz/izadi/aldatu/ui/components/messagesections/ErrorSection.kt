package xyz.izadi.aldatu.ui.components.messagesections

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.ui.components.InfoSection

@Composable
fun ErrorSection(onRetry: () -> Unit, onSecondary: (() -> Unit)? = null) {
    InfoSection(
        icon = Icons.TwoTone.Error,
        text = stringResource(R.string.ms_error_fetching_rates),
        actionSection = {
            Button(onClick = { onRetry() }) {
                Text(
                    text = stringResource(R.string.ms_btn_error_fetching_rates_try_again),
                    textAlign = TextAlign.Center
                )
            }
            onSecondary?.let {
                OutlinedButton(
                    onClick = { onSecondary() },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.ms_btn_error_fetching_not_optimal),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}