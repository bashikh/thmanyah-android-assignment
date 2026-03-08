package com.thmanyah.shasha.domain.repository

import com.thmanyah.shasha.domain.model.Section

interface SearchRepository {
    suspend fun search(query: String): Result<List<Section>>
}
