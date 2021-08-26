package xyz.izadi.aldatu.ui.components.messagesections

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable

@Composable
fun LoadingSection() {
    InfoSection {
        CircularProgressIndicator()
    }
}