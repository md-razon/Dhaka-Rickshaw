package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DhakaDarkScheme = darkColorScheme(
    primary = MarigoldGold,
    onPrimary = InkCharcoal,
    primaryContainer = RickshawCrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = PeacockTeal,
    onSecondary = Color.White,
    secondaryContainer = BengalEmeraldDeep,
    onSecondaryContainer = MarigoldLight,
    tertiary = RickshawCrimson,
    onTertiary = Color.White,
    background = DeepDhakaNight,
    onBackground = WarmCream,
    surface = NightCardSurface,
    onSurface = WarmCream,
    surfaceVariant = Color(0xFF243252),
    onSurfaceVariant = Color(0xFFD5DCEB),
    error = AlertRed,
    onError = Color.White
)

private val DhakaLightScheme = lightColorScheme(
    primary = RickshawCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E3),
    onPrimaryContainer = Color(0xFF5A0024),
    secondary = BengalEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8F5EC),
    onSecondaryContainer = BengalEmeraldDeep,
    tertiary = MarigoldGold,
    onTertiary = InkCharcoal,
    background = WarmCream,
    onBackground = InkCharcoal,
    surface = WarmPaper,
    onSurface = InkCharcoal,
    surfaceVariant = Color(0xFFF6EAD7),
    onSurfaceVariant = MutedSlate,
    error = AlertRed,
    onError = Color.White
)

val DhakaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun DhakaRickshawTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DhakaDarkScheme else DhakaLightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = DhakaShapes,
        content = content
    )
}
