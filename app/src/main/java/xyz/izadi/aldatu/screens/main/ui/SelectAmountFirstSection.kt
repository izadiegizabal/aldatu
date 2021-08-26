package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Paid
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.ui.components.messagesections.InfoSection

@Composable
fun SelectAmountFirstSection() {
    InfoSection(
        icon = Icons.TwoTone.Paid,
        text = stringResource(R.string.ms_info_select_amount)
    )
}