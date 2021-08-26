package xyz.izadi.aldatu.ui.components

import androidx.compose.runtime.Composable
import com.google.accompanist.insets.ProvideWindowInsets
import xyz.izadi.aldatu.ui.theme.AldatuTheme

@Composable
fun BaseContainer(content: @Composable () -> Unit) {
    AldatuTheme {
        ProvideWindowInsets {
            content()
        }
    }
}