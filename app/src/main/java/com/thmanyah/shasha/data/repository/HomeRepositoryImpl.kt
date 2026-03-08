package com.thmanyah.shasha.data.repository

import com.thmanyah.shasha.core.network.safeApiCall
import com.thmanyah.shasha.data.mapper.SectionMapper
import com.thmanyah.shasha.data.remote.HomeApiService
import com.thmanyah.shasha.domain.model.HomeSectionsResult
import com.thmanyah.shasha.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeApiService: HomeApiService,
    private val sectionMapper: SectionMapper,
) : HomeRepository {

    override suspend fun getHomeSections(page: Int): Result<HomeSectionsResult> {
        return safeApiCall { homeApiService.getHomeSections(page) }
            .map { dto -> sectionMapper.mapHomeSectionsResponse(dto) }
    }
}
