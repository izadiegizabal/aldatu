package xyz.izadi.aldatu.screens.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import xyz.izadi.aldatu.R
import xyz.izadi.aldatu.ui.components.Header

@Composable
fun MainHeader(refreshDate: String?) {
    Header(
        title = stringResource(R.string.app_name),
        subtitle = refreshDate?.let { stringResource(R.string.ms_refresh_time, it) }
    )
}