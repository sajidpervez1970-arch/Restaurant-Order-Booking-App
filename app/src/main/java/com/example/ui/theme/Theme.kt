package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LightSageText,
    secondary = OliveGreen,
    tertiary = LightSage,
    primaryContainer = OliveGreen,
    onPrimaryContainer = Color.White,
    background = DarkEarthBg,
    surface = DarkEarthSurface,
    onPrimary = DarkEarthBg,
    onSecondary = Color.White,
    onTertiary = DarkEarthBg,
    onBackground = Color(0xFFE6E2DC),
    onSurface = Color(0xFFE6E2DC),
    surfaceVariant = Color(0xFF2B2A27),
    onSurfaceVariant = SoftStoneGray,
    outline = Color(0xFF494744)
)

private val LightColorScheme = lightColorScheme(
    primary = OliveGreen,
    secondary = OliveGreen,
    tertiary = LightSage,
    primaryContainer = LightSage,
    onPrimaryContainer = OliveGreen,
    background = WarmEarthBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = CharcoalDark,
    onBackground = CharcoalDark,
    onSurface = CharcoalDark,
    surfaceVariant = NavigationBackground,
    onSurfaceVariant = WarmStoneSecondary,
    outline = BorderEarth
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamicColor by default to enforce our beautiful branded Pakistani palette
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
