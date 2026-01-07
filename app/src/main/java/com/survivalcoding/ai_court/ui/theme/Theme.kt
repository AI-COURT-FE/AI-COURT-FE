package com.survivalcoding.ai_court.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = DarkNavy,

    secondary = Brown,
    onSecondary = SoftWhite,

    tertiary = Blue,
    onTertiary = White,

    background = Gray950,
    onBackground = SoftWhite,

    surface = Gray900,
    onSurface = SoftWhite,

    surfaceVariant = Gray800,
    onSurfaceVariant = Gray200,

    outline = Gray600,
    outlineVariant = Gray700,

    error = LightRed,
    onError = DarkNavy,
    errorContainer = DarkRed2,
    onErrorContainer = SoftWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkNavy,
    onPrimary = White,

    secondary = Brown,
    onSecondary = SoftWhite,

    tertiary = Blue,
    onTertiary = White,

    background = Cream,
    onBackground = DarkNavy,

    surface = SoftWhite,
    onSurface = DarkNavy,

    surfaceVariant = Gray50,
    onSurfaceVariant = Gray700,

    outline = Gray300,
    outlineVariant = Gray200,

    error = DarkRed,
    onError = White,
    errorContainer = LightRed,
    onErrorContainer = DarkRed2,
)

@Composable
fun AI_COURTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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

object AI_COURTTheme{
    val colors : AICourtColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAICourtColors.current

    val typography: AI_CourtTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAI_CourtTypography.current
}
