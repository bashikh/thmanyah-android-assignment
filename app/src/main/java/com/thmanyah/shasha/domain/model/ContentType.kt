package com.thmanyah.shasha.domain.model

enum class ContentType {
    PODCAST,
    EPISODE,
    AUDIO_BOOK,
    AUDIO_ARTICLE,
    UNKNOWN;

    companion object {
        fun fromApi(value: String?): ContentType {
            return when (value?.lowercase()) {
                "podcast" -> PODCAST
                "episode" -> EPISODE
                "audio_book" -> AUDIO_BOOK
                "audio_article" -> AUDIO_ARTICLE
                else -> UNKNOWN
            }
        }
    }
}
