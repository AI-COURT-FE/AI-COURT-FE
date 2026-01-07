package com.survivalcoding.ai_court.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.survivalcoding.ai_court.R

val NanumFontLight = FontFamily(Font(R.font.nanumsquare_l))
val NanumFontRegular = FontFamily(Font(R.font.nanumsquare_r))
val NanumFontBold = FontFamily(Font(R.font.nanumsquare_b))

data class AI_CourtTypography(
    val Caption_regular: TextStyle,
    val Caption_tight: TextStyle, // 벌칙 들어주기 설명: 패소자 김논리님은 본 판결에 ...
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
    )
)

val LocalAI_CourtTypography=staticCompositionLocalOf{defaultAI_CourtTypography}
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

