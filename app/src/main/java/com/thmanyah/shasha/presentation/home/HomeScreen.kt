package com.thmanyah.shasha.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thmanyah.shasha.R
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.presentation.components.SectionRenderer
import com.thmanyah.shasha.presentation.components.ShimmerLoadingScreen
import com.thmanyah.shasha.presentation.state.HomeUiState

private enum class HomeCategory(
    val contentType: ContentType?,
    val labelRes: Int,
) {
    ALL(contentType = null, labelRes = R.string.category_all),
    PODCAST(contentType = ContentType.PODCAST, labelRes = R.string.category_podcast),
    AUDIO_BOOK(contentType = ContentType.AUDIO_BOOK, labelRes = R.string.category_audio_books),
    AUDIO_ARTICLE(contentType = ContentType.AUDIO_ARTICLE, labelRes = R.string.category_articles),
    EPISODE(contentType = ContentType.EPISODE, labelRes = R.string.category_episodes),
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HomeUiState.Loading -> ShimmerLoadingScreen(modifier)
        is HomeUiState.Success -> SuccessContent(
            state = state,
            onLoadMore = viewModel::loadMore,
            modifier = modifier,
        )
        is HomeUiState.Error -> ErrorContent(
            messageRes = state.messageRes,
            onRetry = viewModel::retry,
            modifier = modifier,
        )
        is HomeUiState.Empty -> EmptyContent(modifier)
    }
}

@Composable
private fun SuccessContent(
    state: HomeUiState.Success,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val availableCategories = remember(state.sections) { buildAvailableCategories(state.sections) }
    var selectedCategory by rememberSaveable { mutableStateOf(HomeCategory.ALL) }

    LaunchedEffect(availableCategories) {
        if (availableCategories.none { it == selectedCategory }) {
            selectedCategory = HomeCategory.ALL
        }
    }

    val visibleSections = remember(state.sections, selectedCategory) {
        val selectedType = selectedCategory.contentType
        if (selectedType == null) {
            state.sections
        } else {
            state.sections.filter { section -> section.contentType == selectedType }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "category_chips") {
            CategoryChipsRow(
                categories = availableCategories,
                selected = selectedCategory,
                onCategorySelected = { selectedCategory = it },
            )
        }

        if (visibleSections.isEmpty()) {
            item(key = "empty_filtered_state") {
                FilteredEmptyContent()
            }
        }

        itemsIndexed(
            items = visibleSections,
            key = { index, section ->
                "${section.name}_${section.order}_${section.contentType}_$index"
            },
        ) { _, section ->
            SectionRenderer(section = section)
        }

        if (state.isLoadingMore) {
            item {
                val loadingLabel = stringResource(R.string.cd_loading)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics { contentDescription = loadingLabel },
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

private fun buildAvailableCategories(sections: List<Section>): List<HomeCategory> {
    val sectionContentTypes = sections.map { section -> section.contentType }.toSet()
    val categories = mutableListOf(HomeCategory.ALL)
    HomeCategory.values()
        .filter { category ->
            category != HomeCategory.ALL &&
                category.contentType != null &&
                sectionContentTypes.contains(category.contentType)
        }
        .forEach(categories::add)

    return categories
}

@Composable
private fun CategoryChipsRow(
    categories: List<HomeCategory>,
    selected: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = categories,
            key = { it.name },
        ) { category ->
            val isSelected = category == selected
            val chipShape = RoundedCornerShape(percent = 50)
            val unselectedChipColor = MaterialTheme.colorScheme.surfaceVariant
            val selectedLabelColor = Color.White
            val unselectedLabelColor = Color.White.copy(alpha = 0.92f)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.height(34.dp),
                label = {
                    Text(
                        text = stringResource(category.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) selectedLabelColor else unselectedLabelColor,
                    )
                },
                shape = chipShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        unselectedChipColor
                    },
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = selectedLabelColor,
                    containerColor = unselectedChipColor,
                    labelColor = unselectedLabelColor,
                ),
            )
        }
    }
}

@Composable
private fun FilteredEmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_content_available),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ErrorContent(
    @androidx.annotation.StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_content_available),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        )
    }
}
