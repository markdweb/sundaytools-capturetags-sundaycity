package com.sundaytools.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val SundayTypography = Typography(
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp
    )
)
