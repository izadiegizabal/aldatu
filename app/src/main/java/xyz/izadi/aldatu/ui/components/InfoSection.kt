package xyz.izadi.aldatu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Error
import androidx.compose.material.icons.twotone.Paid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.izadi.aldatu.R

@Composable
private fun InfoSection(
    icon: ImageVector? = null,
    text: String? = null,
    actionSection: @Composable() (() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 128.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon?.let {
            Icon(
                it,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(ContentAlpha.high)
            )
        }
        text?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }
        actionSection?.let {
            Row(modifier = Modifier.padding(top = 8.dp)) {
                actionSection()
            }
        }
        content()
    }
}

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

@Composable
fun LoadingSection() {
    InfoSection {
        CircularProgressIndicator()
    }
}

@Composable
fun SelectAnAmountFirst() {
    InfoSection(
        icon = Icons.TwoTone.Paid,
        text = stringResource(R.string.ms_info_select_amount)
    )
}