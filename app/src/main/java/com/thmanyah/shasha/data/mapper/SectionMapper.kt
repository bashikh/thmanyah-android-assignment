package com.thmanyah.shasha.data.mapper

import android.util.Log
import com.thmanyah.shasha.core.util.toSafeInt
import com.thmanyah.shasha.data.remote.dto.HomeSectionsResponseDto
import com.thmanyah.shasha.data.remote.dto.SectionDto
import com.thmanyah.shasha.domain.model.ContentType
import com.thmanyah.shasha.domain.model.HomeSectionsResult
import com.thmanyah.shasha.domain.model.LayoutType
import com.thmanyah.shasha.domain.model.Section
import javax.inject.Inject

class SectionMapper @Inject constructor(
    private val contentMapper: ContentMapper,
) {

    fun mapHomeSectionsResponse(dto: HomeSectionsResponseDto): HomeSectionsResult {
        val sections = dto.sections
            ?.mapIndexedNotNull { index, sectionDto ->
                mapSection(sectionDto, index)
            }
            ?.sortedBy { it.order }
            ?: emptyList()

        return HomeSectionsResult(
            sections = sections,
            nextPage = dto.pagination?.nextPage,
            totalPages = dto.pagination?.totalPages,
        )
    }

    fun mapSections(dtos: List<SectionDto>?): List<Section> {
        return dtos
            ?.mapIndexedNotNull { index, sectionDto ->
                mapSection(sectionDto, index)
            }
            ?.sortedBy { it.order }
            ?: emptyList()
    }

    private fun mapSection(dto: SectionDto, index: Int): Section? {
        val contentType = ContentType.fromApi(dto.contentType)
        val items = dto.content?.mapIndexedNotNull { itemIndex, rawItem ->
            contentMapper.mapItem(rawItem, contentType, itemIndex)
        } ?: emptyList()

        if (items.isEmpty()) {
            Log.w("SectionMapper", "Skipping section '${dto.name}': no valid items after mapping")
            return null
        }

        return Section(
            name = dto.name ?: "",
            layoutType = LayoutType.fromApi(dto.type),
            contentType = contentType,
            order = dto.order.toSafeInt(index),
            items = items,
        )
    }
}
