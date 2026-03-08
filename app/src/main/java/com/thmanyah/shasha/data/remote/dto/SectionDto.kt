package com.thmanyah.shasha.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SectionDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "content_type") val contentType: String? = null,
    @Json(name = "order") val order: Any? = null,
    @Json(name = "content") val content: List<Map<String, Any?>>? = null,
)
