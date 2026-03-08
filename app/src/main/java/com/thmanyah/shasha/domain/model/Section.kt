package com.thmanyah.shasha.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Section(
    val name: String,
    val layoutType: LayoutType,
    val contentType: ContentType,
    val order: Int,
    val items: List<SectionItem>,
)

@Immutable
data class HomeSectionsResult(
    val sections: List<Section>,
    val nextPage: String?,
    val totalPages: Int?,
)
