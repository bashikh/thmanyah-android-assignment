package com.thmanyah.shasha.data.mapper

import android.util.Log
import com.thmanyah.shasha.core.util.toSafeDouble
import com.thmanyah.shasha.core.util.toSafeInt
import com.thmanyah.shasha.core.util.toSafeLong
import com.thmanyah.shasha.core.util.toSafeString
import com.thmanyah.shasha.domain.model.ArticleItem
import com.thmanyah.shasha.domain.model.AudioBookItem
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.EpisodeItem
import com.thmanyah.shasha.domain.model.GenericItem
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.SectionItem
import javax.inject.Inject

class ContentMapper @Inject constructor() {

    fun mapItem(raw: Map<String, Any?>, contentType: ContentType, index: Int): SectionItem? {
        return try {
            when (contentType) {
                ContentType.PODCAST -> mapPodcast(raw, index)
                ContentType.EPISODE -> mapEpisode(raw, index)
                ContentType.AUDIO_BOOK -> mapAudioBook(raw, index)
                ContentType.AUDIO_ARTICLE -> mapArticle(raw, index)
                ContentType.UNKNOWN -> mapFallbackItem(raw, index)
            }
        } catch (e: Exception) {
            Log.w("ContentMapper", "Skipping invalid item at index $index: ${e.message}")
            null
        }
    }

    private fun mapPodcast(raw: Map<String, Any?>, index: Int): PodcastItem {
        return PodcastItem(
            id = raw["podcast_id"].toSafeString("podcast_$index"),
            name = raw["name"].toSafeString(),
            avatarUrl = raw["avatar_url"].toSafeString(),
            description = raw["description"].toSafeString(),
            duration = raw["duration"].toSafeLong(),
            episodeCount = raw["episode_count"].toSafeInt(),
            language = raw["language"].toSafeString(),
            score = raw["score"].toSafeDouble(),
        )
    }

    private fun mapEpisode(raw: Map<String, Any?>, index: Int): EpisodeItem {
        return EpisodeItem(
            id = raw["episode_id"].toSafeString("episode_$index"),
            name = raw["name"].toSafeString(),
            avatarUrl = raw["avatar_url"].toSafeString(),
            description = raw["description"].toSafeString(),
            duration = raw["duration"].toSafeLong(),
            podcastName = raw["podcast_name"].toSafeString(),
            episodeType = raw["episode_type"].toSafeString(),
            releaseDate = raw["release_date"].toSafeString(),
            audioUrl = raw["audio_url"].toSafeString(),
            podcastId = raw["podcast_id"].toSafeString(),
            score = raw["score"].toSafeDouble(),
        )
    }

    private fun mapAudioBook(raw: Map<String, Any?>, index: Int): AudioBookItem {
        return AudioBookItem(
            id = raw["audiobook_id"].toSafeString("audiobook_$index"),
            name = raw["name"].toSafeString(),
            avatarUrl = raw["avatar_url"].toSafeString(),
            description = raw["description"].toSafeString(),
            duration = raw["duration"].toSafeLong(),
            authorName = raw["author_name"].toSafeString(),
            language = raw["language"].toSafeString(),
            releaseDate = raw["release_date"].toSafeString(),
            score = raw["score"].toSafeDouble(),
        )
    }

    private fun mapArticle(raw: Map<String, Any?>, index: Int): ArticleItem {
        return ArticleItem(
            id = raw["article_id"].toSafeString("article_$index"),
            name = raw["name"].toSafeString(),
            avatarUrl = raw["avatar_url"].toSafeString(),
            description = raw["description"].toSafeString(),
            duration = raw["duration"].toSafeLong(),
            authorName = raw["author_name"].toSafeString(),
            releaseDate = raw["release_date"].toSafeString(),
            score = raw["score"].toSafeDouble(),
        )
    }

    private fun mapFallbackItem(raw: Map<String, Any?>, index: Int): SectionItem {
        val id = raw["podcast_id"] ?: raw["episode_id"] ?: raw["audiobook_id"] ?: raw["article_id"]
        return GenericItem(
            id = id.toSafeString("unknown_$index"),
            name = raw["name"].toSafeString(),
            avatarUrl = raw["avatar_url"].toSafeString(),
            description = raw["description"].toSafeString(),
            duration = raw["duration"].toSafeLong(),
        )
    }
}
