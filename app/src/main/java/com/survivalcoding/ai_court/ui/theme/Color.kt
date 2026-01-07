package com.survivalcoding.ai_court.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Color.kt
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val SoftWhite = Color(0xFFFDFDFD)
val Gray50 = Color(0xFFF2F3F6)
val Gray100 = Color(0xFFE2E3E9)
val Gray200 = Color(0xFFD5DAE3)
val Gray300 = Color(0xFFB6BED2)
val Gray400 = Color(0xFF8B91A1)
val Gray500 = Color(0xFF6B7084)
val Gray600 = Color(0xFF4E5368)
val Gray700 = Color(0xFF383E53)
val Gray800 = Color(0xFF262A3D)
val Gray900 = Color(0xFF1C1F2D)
val Gray950 = Color(0xFF191A28)
val SoftGray = Color(0xFF333333)

val Cream = Color(0xFFF2EDD7) // 배경색

val Brown = Color(0xFF755139)
val RedBrown = Color(0xFF914E3B)

val Navy = Color(0xFF25354F)
val DarkNavy = Color(0xFF292D47)
val Blue = Color(0xFF0C2F86)
val LightBlue = Color(0xFFBAD6FB)

val LightRed = Color(0xFFFBBABB)
val DarkRed = Color(0xFFC62E1B)
val DarkRed2 = Color(0xFFBB1316)

val Orange = Color(0xFFF6B166)

val Yellow1 = Color(0xFFFFAC33)
val Yellow2 = Color(0xFF99671F)

data class AICourtColors(
    val white: Color,
    val black: Color,

    val softWhite: Color,
    val softGray: Color,

    val cream: Color,

    val brown: Color,
    val redBrown: Color,

    val navy: Color,
    val darkNavy: Color,

    val blue: Color,
    val lightBlue: Color,

    val lightRed: Color,
    val darkRed: Color,
    val darkRed2: Color,

    val orange: Color,

    val yellow1: Color,
    val yellow2: Color,

    val gray50: Color,
    val gray100: Color,
    val gray200: Color,
    val gray300: Color,
    val gray400: Color,
    val gray500: Color,
    val gray600: Color,
    val gray700: Color,
    val gray800: Color,
    val gray900: Color,
    val gray950: Color,
)

val defaultAICourtColors = AICourtColors(
    white = White,
    black = Black,

    softWhite = SoftWhite,
    softGray = SoftGray,

    cream = Cream,

    brown = Brown,
    redBrown = RedBrown,

    navy = Navy,
    darkNavy = DarkNavy,

    blue = Blue,
    lightBlue = LightBlue,

    lightRed = LightRed,
    darkRed = DarkRed,
    darkRed2 = DarkRed2,

    orange = Orange,

    yellow1 = Yellow1,
    yellow2 = Yellow2,

    gray50 = Gray50,
    gray100 = Gray100,
    gray200 = Gray200,
    gray300 = Gray300,
    gray400 = Gray400,
    gray500 = Gray500,
    gray600 = Gray600,
    gray700 = Gray700,
    gray800 = Gray800,
    gray900 = Gray900,
    gray950 = Gray950,
)

val LocalAICourtColors = staticCompositionLocalOf {defaultAICourtColors}