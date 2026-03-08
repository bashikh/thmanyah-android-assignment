package com.thmanyah.shasha.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.tooling.preview.Preview
import com.thmanyah.shasha.R
import com.thmanyah.shasha.domain.model.ArticleItem
import com.thmanyah.shasha.ui.theme.ThmanyahTheme
import com.thmanyah.shasha.domain.model.AudioBookItem
import com.thmanyah.shasha.domain.model.EpisodeItem
import com.thmanyah.shasha.domain.model.GenericItem
import com.thmanyah.shasha.domain.model.PodcastItem
import com.thmanyah.shasha.domain.model.SectionItem

@Composable
fun BigSquareSection(
    items: List<SectionItem>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(items, key = { index, item -> "${item.id}_$index" }) { _, item ->
            BigSquareCard(item = item)
        }
    }
}

@Composable
private fun BigSquareCard(
    item: SectionItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(180.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.avatarUrl)
                .crossfade(true)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .build(),
            contentDescription = item.name,
            modifier = Modifier
                .width(180.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        val subtitle = getSubtitle(item)
        if (!subtitle.isNullOrEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else if (item is PodcastItem && item.episodeCount > 0) {
            Text(
                text = stringResource(R.string.episode_count_format, item.episodeCount),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun getSubtitle(item: SectionItem): String? {
    return when (item) {
        is PodcastItem -> null
        is EpisodeItem -> item.podcastName
        is AudioBookItem -> item.authorName
        is ArticleItem -> item.authorName
        is GenericItem -> null
    }
}

@Preview
@Composable
private fun BigSquareSectionPreview() {
    ThmanyahTheme {
        Surface {
            BigSquareSection(items = PreviewData.audioBooks)
        }
    }
}
