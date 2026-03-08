package com.thmanyah.shasha.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.ui.theme.ThmanyahTheme

@Composable
fun SectionRenderer(
    section: Section,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = section.name,
            onSeeAllClick = { },
        )

        when (section.layoutType) {
            LayoutType.SQUARE -> SquareSection(items = section.items)
            LayoutType.BIG_SQUARE -> BigSquareSection(items = section.items)
            LayoutType.TWO_LINES_GRID -> TwoLinesGridSection(items = section.items)
            LayoutType.QUEUE -> QueueSection(items = section.items)
            LayoutType.UNKNOWN -> FallbackSection(items = section.items)
        }
    }
}

@Preview
@Composable
private fun SectionRendererPreview() {
    ThmanyahTheme {
        Surface {
            SectionRenderer(section = PreviewData.squareSection)
        }
    }
}
