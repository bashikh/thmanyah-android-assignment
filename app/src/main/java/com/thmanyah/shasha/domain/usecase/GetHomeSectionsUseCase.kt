package com.thmanyah.shasha.domain.usecase

import com.thmanyah.shasha.domain.model.HomeSectionsResult
import com.thmanyah.shasha.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeSectionsUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(page: Int = 1): Result<HomeSectionsResult> {
        return homeRepository.getHomeSections(page)
    }
}
