package com.thmanyah.shasha.domain.repository

import com.thmanyah.shasha.domain.model.HomeSectionsResult

interface HomeRepository {
    suspend fun getHomeSections(page: Int = 1): Result<HomeSectionsResult>
}
