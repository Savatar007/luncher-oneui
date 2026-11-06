package com.example.luncher.theme

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// NOTE: a OneUiTokens data class existed here in the original with invalid
// types (":.dp" / "dp" used as a type instead of Dp). Nothing referenced it —
// the live tokens are the OneUi object at the bottom of this file — so it was
// removed rather than repaired.

private val LightOneUiColors = lightColorScheme(
    primary = Color(0xFF0072DE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F0FF),
    onPrimaryContainer = Color(0xFF003D66),
    secondary = Color(0xFF6A7C8C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5F0FF),
    onSecondaryContainer = Color(0xFF475A6C),
    tertiary = Color(0xFF7256C6),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE9E0FF),
    onTertiaryContainer = Color(0xFF3C1F8C),
    error = Color(0xFFC5221F),
    errorContainer = Color(0xFFFFDAD5),
    onError = Color.White,
    onErrorContainer = Color(0xFF8A0000),
    background = Color(0xFFF6F6F6),
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFF2F3F4),
    onSurfaceVariant = Color(0xFF6D6D6D),
    outline = Color(0xFFD1D5DA),
    inverseOnSurface = Color(0xFFF5F5F5),
    inverseSurface = Color(0xFF2B2B2B),
    inversePrimary = Color(0xFF7CA9FF),
)

private val DarkOneUiColors = darkColorScheme(
    primary = Color(0xFF3E91FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004376),
    onPrimaryContainer = Color(0xFFE3F0FF),
    secondary = Color(0xFFA3A3A3),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF404C56),
    onSecondaryContainer = Color(0xFFE8E9DA),
    tertiary = Color(0xFFB58AFF),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF503F8D),
    onTertiaryContainer = Color(0xFFFFE8FF),
    error = Color(0xFFFF8D8D),
    errorContainer = Color(0xFF891B1B),
    onError = Color.Black,
    onErrorContainer = Color(0xFFFFD7D7),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFBFF),
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFF454B53),
    onSurfaceVariant = Color(0xFFA8ADB4),
    outline = Color(0xFF797C80),
    inverseOnSurface = Color(0xFF2B2B2B),
    inverseSurface = Color(0xFFFFFBFF),
    inversePrimary = Color(0xFF7CA9FF),
)

val OneUiBlue = Color(0xFF3E91FF)
val OneUiGreen = Color(0xFF2FBF71)
val OneUiOrange = Color(0xFFFF8A3D)
val OneUiPurple = Color(0xFF8E6BFF)
val OneUiTeal = Color(0xFF17B6C4)
val OneUiPink = Color(0xFFFF5C8A)
val OneUiGrey = Color(0xFF8B8B90)

val OneUiSwitchTrackOff = Color(0xFFE4E4E6)
val OneUiSwitchTrackOffDark = Color(0xFF2C2C2E)

val LightDivider = Color(0x14000000)
val DarkDivider = Color(0x1AFFFFFF)

val SystemFont = FontFamily.Default

@Composable
fun OneUiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkOneUiColors
        else -> LightOneUiColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OneUiTypography(),
        shapes = OneUiShapes(),
        content = content
    )
}

@Composable
fun isSystemInDarkTheme(): Boolean {
    return LocalContext.current.resources.configuration.uiMode
        .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun OneUiTypography(): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontSize = 56.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        displayMedium = TextStyle(
            fontSize = 48.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        displaySmall = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        headlineLarge = TextStyle(
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        headlineMedium = TextStyle(
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        headlineSmall = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        titleLarge = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        titleMedium = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = SystemFont
        ),
        bodyLarge = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        bodyMedium = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        bodySmall = TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = SystemFont
        ),
        labelLarge = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SystemFont
        ),
        labelMedium = TextStyle(
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SystemFont
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SystemFont
        )
    )
}

fun OneUiShapes(): Shapes {
    return Shapes(
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(28.dp)
    )
}

object OneUi {
    val CardRadius = 28.dp
    val DialogRadius = 32.dp
    val ListRowMinHeight = 60.dp
    val IconChipSize = 38.dp
    val PillHeight = 56.dp
    val BottomPadding = 140.dp
    val FadeGradientHeight = 120.dp

    val CollapsingToolbarExpandedHeight = 280.dp
    val CollapsingToolbarCollapsedHeight = 56.dp
    val TitleLargeSize = 34.sp
    val TitleMediumSize = 20.sp

    val BackButtonSize = 48.dp
    val BackButtonBlurSigma = 28.dp

    val FastEase = tween<Float>(durationMillis = 100)
    val ElementEase = tween<Float>(durationMillis = 250)
    val PageEase = tween<Float>(durationMillis = 350)

    fun glassLight(alpha: Float = 0.62f): Color = Color(0xFFFFFF).copy(alpha = alpha)
    fun glassDark(alpha: Float = 0.55f): Color = Color(0xFF28282A).copy(alpha = alpha)
}
