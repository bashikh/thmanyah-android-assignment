package com.thmanyah.shasha.domain.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SectionItem {
    val id: String
    val name: String
    val avatarUrl: String
    val description: String
    val duration: Long
}

@Immutable
data class PodcastItem(
    override val id: String,
    override val name: String,
    override val avatarUrl: String,
    override val description: String,
    override val duration: Long,
    val episodeCount: Int,
    val language: String,
    val score: Double,
) : SectionItem

@Immutable
data class EpisodeItem(
    override val id: String,
    override val name: String,
    override val avatarUrl: String,
    override val description: String,
    override val duration: Long,
    val podcastName: String,
    val episodeType: String,
    val releaseDate: String,
    val audioUrl: String,
    val podcastId: String,
    val score: Double,
) : SectionItem

@Immutable
data class AudioBookItem(
    override val id: String,
    override val name: String,
    override val avatarUrl: String,
    override val description: String,
    override val duration: Long,
    val authorName: String,
    val language: String,
    val releaseDate: String,
    val score: Double,
) : SectionItem

@Immutable
data class ArticleItem(
    override val id: String,
    override val name: String,
    override val avatarUrl: String,
    override val description: String,
    override val duration: Long,
    val authorName: String,
    val releaseDate: String,
    val score: Double,
) : SectionItem

@Immutable
data class GenericItem(
    override val id: String,
    override val name: String,
    override val avatarUrl: String,
    override val description: String,
    override val duration: Long,
) : SectionItem
