package com.fullwar.menuapp.presentation.common.utils

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.fullwar.menuapp.R

fun fontFamilyFromString(name: String): FontFamily = when (name) {
    "serif"          -> FontFamily.Serif
    "sans_serif"     -> FontFamily.SansSerif
    "monospace"      -> FontFamily.Monospace
    "fredericka"     -> FontFamily(Font(R.font.frederickathe_great_regular))
    "rubik_microbe"  -> FontFamily(Font(R.font.rubik_microbe_regular))
    "chango"         -> FontFamily(Font(R.font.chango_regular))
    else             -> FontFamily.Default
}
