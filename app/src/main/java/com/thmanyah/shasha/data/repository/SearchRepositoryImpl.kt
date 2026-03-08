package com.thmanyah.shasha.data.repository

import com.thmanyah.shasha.core.network.safeApiCall
import com.thmanyah.shasha.data.mapper.SectionMapper
import com.thmanyah.shasha.data.remote.SearchApiService
import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchApiService: SearchApiService,
    private val sectionMapper: SectionMapper,
) : SearchRepository {

    override suspend fun search(query: String): Result<List<Section>> {
        return safeApiCall { searchApiService.search(query) }
            .map { dto -> sectionMapper.mapSections(dto.sections) }
    }
}
