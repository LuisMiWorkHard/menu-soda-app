package com.fullwar.menuapp.presentation.common.utils

import java.text.Normalizer

object FuzzyMatcher {
    private val STOPWORDS = setOf(
        "el", "la", "los", "las", "de", "del", "en", "al", "a",
        "con", "y", "e", "o", "u", "un", "una", "unos", "unas", "por", "para"
    )

    fun normalize(text: String): String {
        val nfd = Normalizer.normalize(text, Normalizer.Form.NFD)
        return nfd.replace(Regex("[^\\p{ASCII}]"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() && it !in STOPWORDS }
            .joinToString(" ")
    }

    fun levenshtein(a: String, b: String): Int {
        val m = a.length; val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) for (j in 1..n) {
            dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
            else 1 + minOf(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])
        }
        return dp[m][n]
    }

    fun jaccard(a: String, b: String): Double {
        val sa = a.split(" ").filter { it.isNotBlank() }.toHashSet()
        val sb = b.split(" ").filter { it.isNotBlank() }.toHashSet()
        if (sa.isEmpty() && sb.isEmpty()) return 1.0
        val intersection = sa.count { it in sb }
        val union = sa.size + sb.size - intersection
        return if (union == 0) 0.0 else intersection.toDouble() / union
    }

    fun isDuplicate(query: String, candidate: String): Boolean {
        val q = normalize(query)
        val c = normalize(candidate)
        if (q.isEmpty() || c.isEmpty()) return false
        if (q == c) return true
        if (jaccard(q, c) >= 0.60) return true
        if (q.length >= 4 && c.length >= 4 && levenshtein(q, c) <= 1) return true
        return false
    }
}
