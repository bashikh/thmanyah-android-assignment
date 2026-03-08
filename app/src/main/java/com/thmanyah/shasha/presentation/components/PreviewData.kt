package com.thmanyah.shasha.presentation.components

import com.thmanyah.shasha.domain.model.ArticleItem
import com.thmanyah.shasha.domain.model.AudioBookItem
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.EpisodeItem
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.Section

internal object PreviewData {

    val podcast = PodcastItem(
        id = "podcast_1",
        name = "Up First from NPR",
        avatarUrl = "",
        description = "The news you need to start your day.",
        duration = 474313,
        episodeCount = 500,
        language = "en",
        score = 217.0,
    )

    val episode = EpisodeItem(
        id = "episode_1",
        name = "NPR Politics Live From Cleveland",
        avatarUrl = "",
        description = "A special episode recorded in front of a live audience.",
        duration = 1846,
        podcastName = "The NPR Politics Podcast",
        episodeType = "full",
        releaseDate = "2018-02-24",
        audioUrl = "",
        podcastId = "1057255460",
        score = 216.0,
    )

    val audioBook = AudioBookItem(
        id = "audiobook_1",
        name = "The Art of War",
        avatarUrl = "",
        description = "An ancient military text on strategy and tactics.",
        duration = 36000,
        authorName = "Sun Tzu",
        language = "en",
        releaseDate = "2023-01-10",
        score = 500.0,
    )

    val article = ArticleItem(
        id = "article_1",
        name = "The Future of AI",
        avatarUrl = "",
        description = "An in-depth look at the future impact of artificial intelligence.",
        duration = 1200,
        authorName = "Tech World",
        releaseDate = "2023-05-10",
        score = 300.0,
    )

    val podcasts = List(5) { podcast.copy(id = "podcast_$it", name = "Podcast ${it + 1}") }

    val episodes = List(6) { episode.copy(id = "episode_$it", name = "Episode ${it + 1}") }

    val audioBooks = List(5) { audioBook.copy(id = "audiobook_$it") }

    val squareSection = Section(
        name = "Top Podcasts",
        layoutType = LayoutType.SQUARE,
        contentType = ContentType.PODCAST,
        order = 1,
        items = podcasts,
    )

    val bigSquareSection = Section(
        name = "Bestselling Audiobooks",
        layoutType = LayoutType.BIG_SQUARE,
        contentType = ContentType.AUDIO_BOOK,
        order = 3,
        items = audioBooks,
    )

    val twoLinesGridSection = Section(
        name = "Trending Episodes",
        layoutType = LayoutType.TWO_LINES_GRID,
        contentType = ContentType.EPISODE,
        order = 2,
        items = episodes,
    )

    val queueSection = Section(
        name = "New Podcasts",
        layoutType = LayoutType.QUEUE,
        contentType = ContentType.PODCAST,
        order = 5,
        items = podcasts,
    )

    val allSections = listOf(squareSection, twoLinesGridSection, bigSquareSection, queueSection)
}
