package com.example.healthmedicareapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.healthmedicareapp.R

// ── Poppins via Google Fonts downloadable-font API ────────────────────────────
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val PoppinsFont = GoogleFont("Poppins")

val PoppinsFamily = FontFamily(
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = PoppinsFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

// ── Typography ────────────────────────────────────────────────────────────────
val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 32.sp, lineHeight = 40.sp),
    titleLarge    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold,     fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelSmall    = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp),
)

// ── Light color scheme ────────────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary          = Blue700,
    onPrimary        = purewhite,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFD6E4FF),
    secondary        = Green700,
    onSecondary      = purewhite,
    background       = BackgroundLight,
    onBackground     = OnBgLight,
    surface          = SurfaceLight,
    onSurface        = OnBgLight,
    surfaceVariant   = BackgroundLight,
    outline          = androidx.compose.ui.graphics.Color(0xFFCCCCCC),
)

// ── Dark color scheme ─────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary          = BlueAccent,
    onPrimary        = BackgroundDark,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF1C2A4A),
    secondary        = Green400,
    onSecondary      = BackgroundDark,
    background       = BackgroundDark,
    onBackground     = OnBgDark,
    surface          = SurfaceDark,
    onSurface        = OnBgDark,
    surfaceVariant   = Surface2Dark,
    outline          = androidx.compose.ui.graphics.Color(0xFF30363D),
)

// ── Theme entry-point ─────────────────────────────────────────────────────────
@Composable
fun HealthMedicareTheme(
    darkTheme: Boolean  = false,
    toggleDark: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    CompositionLocalProvider(
        LocalDarkMode provides DarkModeState(isDark = darkTheme, toggle = toggleDark)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            content     = content
        )
    }
}