package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val NexoraDarkColorScheme =
    darkColorScheme(
        primary = NexoraPrimary,
        onPrimary = NexoraTextPrimary,
        primaryContainer = NexoraPrimaryVariant,
        secondary = NexoraSecondary,
        onSecondary = NexoraDarkBackground,
        background = NexoraDarkBackground,
        onBackground = NexoraTextPrimary,
        surface = NexoraDarkSurface,
        onSurface = NexoraTextPrimary,
        surfaceVariant = NexoraDarkSurfaceVariant,
        onSurfaceVariant = NexoraTextSecondary,
        error = NexoraError,
        onError = NexoraTextPrimary
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to premium dark theme for Nexora AI
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> NexoraDarkColorScheme
            else -> NexoraDarkColorScheme // Enforce dark theme consistency for Nexora AI
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
