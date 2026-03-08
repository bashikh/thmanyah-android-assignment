package com.thmanyah.shasha.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeSectionsResponseDto(
    @Json(name = "sections") val sections: List<SectionDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null,
)

@JsonClass(generateAdapter = true)
data class PaginationDto(
    @Json(name = "next_page") val nextPage: String? = null,
    @Json(name = "total_pages") val totalPages: Int? = null,
)
