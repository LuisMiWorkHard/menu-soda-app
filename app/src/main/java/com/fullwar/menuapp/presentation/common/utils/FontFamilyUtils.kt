package com.fullwar.menuapp.presentation.common.utils

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.fullwar.menuapp.R

fun fontFamilyFromString(name: String): FontFamily = when (name) {
    "serif"      -> FontFamily.Serif
    "sans_serif" -> FontFamily.SansSerif
    "monospace"  -> FontFamily.Monospace
    "fredericka" -> FontFamily(Font(R.font.frederickathe_great_regular))
    else         -> FontFamily.Default
}
