package xyz.izadi.aldatu.ui.components.messagesections

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import xyz.izadi.aldatu.ui.components.InfoSection

@Composable
fun LoadingSection() {
    InfoSection {
        CircularProgressIndicator()
    }
}