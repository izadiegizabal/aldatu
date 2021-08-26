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

@Composable
fun ErrorSection(
    primaryText: String?,
    secondaryText: String?,
    onPrimary: (() -> Unit)?,
    onSecondary: (() -> Unit)? = null
) {
    InfoSection(
        icon = Icons.TwoTone.Error,
        text = stringResource(R.string.ms_error_fetching_rates),
        actionSection = {
            onPrimary?.takeIf { primaryText != null }?.let {
                Button(onClick = { onPrimary() }) {
                    Text(
                        text = primaryText ?: "",
                        textAlign = TextAlign.Center
                    )
                }
            }
            onSecondary?.takeIf { secondaryText != null }?.let {
                OutlinedButton(
                    onClick = { onSecondary() },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = secondaryText ?: "",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}