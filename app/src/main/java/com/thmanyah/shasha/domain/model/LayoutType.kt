package com.thmanyah.shasha.domain.model

enum class LayoutType {
    SQUARE,
    BIG_SQUARE,
    TWO_LINES_GRID,
    QUEUE,
    UNKNOWN;

    companion object {
        fun fromApi(value: String?): LayoutType {
            val normalized = value?.lowercase()?.replace(" ", "_") ?: return UNKNOWN
            return when (normalized) {
                "square" -> SQUARE
                "big_square" -> BIG_SQUARE
                "2_lines_grid" -> TWO_LINES_GRID
                "queue" -> QUEUE
                else -> UNKNOWN
            }
        }
    }
}
