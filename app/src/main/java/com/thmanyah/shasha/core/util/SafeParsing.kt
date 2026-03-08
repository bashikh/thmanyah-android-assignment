package com.thmanyah.shasha.core.util

fun Any?.toSafeInt(default: Int = 0): Int {
    return when (this) {
        is Number -> toInt()
        is String -> toIntOrNull() ?: default
        else -> default
    }
}

fun Any?.toSafeLong(default: Long = 0L): Long {
    return when (this) {
        is Number -> toLong()
        is String -> toLongOrNull() ?: default
        else -> default
    }
}

fun Any?.toSafeDouble(default: Double = 0.0): Double {
    return when (this) {
        is Number -> toDouble()
        is String -> toDoubleOrNull() ?: default
        else -> default
    }
}

fun Any?.toSafeString(default: String = ""): String {
    return this?.toString() ?: default
}
