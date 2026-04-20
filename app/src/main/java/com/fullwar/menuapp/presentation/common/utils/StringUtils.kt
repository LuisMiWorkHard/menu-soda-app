package com.fullwar.menuapp.presentation.common.utils

private val STOP_WORDS = setOf(
    "de", "del", "la", "las", "el", "los", "y", "e", "o", "u", "ni",
    "con", "a", "en", "al", "un", "una", "unos", "unas",
    "que", "por", "para", "sin", "sobre", "entre", "hacia",
    "hasta", "desde", "tras", "durante", "según", "como"
)

fun String.toSmartUpperCase(): String {
    val words = this.lowercase().split(" ")
    return words.mapIndexed { i, word ->
        if (i == 0 || word !in STOP_WORDS) word.replaceFirstChar { it.uppercase() }
        else word
    }.joinToString(" ")
}
