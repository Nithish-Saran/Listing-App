package com.listingapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.listingapp.R

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight(500),
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 1.0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight(500),
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 1.0.sp
    ),
    // h6
    headlineSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.00075.em
    ),
    //subtitle 1
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.00938.em
    ),
//    titleMedium = TextStyle(
//        fontFamily = FontFamily(Font(R.font.poppins_regular)),
//        fontWeight = FontWeight(400),
//        fontSize = 12.sp,
//        lineHeight = 18.sp,
//        letterSpacing = 0.4.sp
//    ),
    //subtitle 2
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.00714.em
    ),
    //body 1
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.00938.em
    ),
    //body 2
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01071.em
    ),
    /*    bodyMedium = TextStyle(
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            fontWeight = FontWeight(400),
            fontSize = 8.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.5.sp
        ),*/
    //caption
    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.03333.em
    ),
    //button
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight(500),
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.02857.em
    ),
    //overline
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppins_regular)),
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.08333.em
    ),
)