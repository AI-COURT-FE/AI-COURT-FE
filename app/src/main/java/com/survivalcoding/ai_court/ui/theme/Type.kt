package com.survivalcoding.ai_court.ui.theme


import android.R.attr.fontFamily
import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.R
import retrofit2.http.Body

val NanumFontLight = FontFamily(Font(R.font.nanumsquare_l))
val NanumFontRegular = FontFamily(Font(R.font.nanumsquare_r))
val NanumFontBold = FontFamily(Font(R.font.nanumsquare_b))
val OktapbangFont = FontFamily(Font(R.font.oktapbang))

data class AI_CourtTypography(
    val Caption_regular: TextStyle,
    val Caption_tight: TextStyle, // 벌칙 들어주기 설명: 패소자 김논리님은 본 판결에 ...
    val Caption_3: TextStyle,
    val Title_1: TextStyle,
    val Title_2: TextStyle,
    val Body_1: TextStyle,
    val Body_2: TextStyle,
    val Body_3: TextStyle,
    val Body_4: TextStyle,
)

val defaultAI_CourtTypography = AI_CourtTypography(
    Caption_regular = TextStyle(
        fontFamily = NanumFontRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    Caption_tight = TextStyle(
        fontFamily = NanumFontRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 15.sp
    ),
    Caption_3 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.0025).em,
    ),
    Title_1 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 35.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.0025).em,
    ),
    Title_2 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.0025).em,
    ),
    Body_1 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.0025).em,
    ),
    Body_2 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    Body_3 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp,
    ),
    Body_4 = TextStyle(
        fontFamily = OktapbangFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 17.sp,
    )
)

val LocalAI_CourtTypography = staticCompositionLocalOf { defaultAI_CourtTypography }
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

