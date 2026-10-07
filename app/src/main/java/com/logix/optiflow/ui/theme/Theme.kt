package com.logix.optiflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OptiFlowColorScheme =
    lightColorScheme(
        primary = OptiFlowBlue,
        onPrimary = Color.White,
        primaryContainer = OptiFlowBlueDark,
        secondary = OptiFlowBlueDark,
        background = OptiFlowSurface,
        onBackground = OptiFlowOnSurface,
        surface = Color.White,
        onSurface = OptiFlowOnSurface,
    )

@Composable
fun OptiFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OptiFlowColorScheme,
        content = content,
    )
}
