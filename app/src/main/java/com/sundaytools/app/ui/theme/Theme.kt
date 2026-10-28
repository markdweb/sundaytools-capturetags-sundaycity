package com.sundaytools.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = SundayGreenPrimary,
    onPrimary = SundayWhite,
    secondary = SundayGold,
    background = SundayBackgroundDark,
    surface = SundaySurfaceDark,
    surfaceVariant = SundaySurfaceVariantDark,
    onBackground = SundayOnDark,
    onSurface = SundayOnDark,
    onSurfaceVariant = SundayOnDark.copy(alpha = 0.7f),
    error = SundayError
)

private val LightColors = lightColorScheme(
    primary = SundayGreenPrimary,
    onPrimary = SundayWhite,
    secondary = SundayGold,
    background = SundayBackgroundLight,
    surface = SundaySurfaceLight,
    surfaceVariant = SundaySurfaceVariantLight,
    onBackground = SundayOnLight,
    onSurface = SundayOnLight,
    onSurfaceVariant = SundayOnLight.copy(alpha = 0.6f),
    error = SundayError
)

/**
 * App-wide theme. Defaults to light mode (matches the app's default Dark
 * Mode setting being off); [darkTheme] is normally passed explicitly from
 * the user's Settings, using a true-black background rather than dark gray.
 */
@Composable
fun SundayToolsTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SundayTypography,
        content = content
    )
}
