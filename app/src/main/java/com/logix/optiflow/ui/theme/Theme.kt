package com.logix.optiflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OptiFlowColorScheme =
    lightColorScheme(
        primary = OptiFlowNavy,
        onPrimary = Color.White,
        primaryContainer = OptiFlowBlue,
        secondary = OptiFlowBlueDark,
        background = OptiFlowGradientTop,
        onBackground = OptiFlowNavy,
        surface = Color.White,
        onSurface = OptiFlowNavy,
    )

@Composable
fun OptiFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OptiFlowColorScheme,
        content = content,
    )
}
