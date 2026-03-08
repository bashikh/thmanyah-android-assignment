package com.thmanyah.shasha.domain.usecase

import com.thmanyah.shasha.domain.model.Section
import com.thmanyah.shasha.domain.repository.SearchRepository
import javax.inject.Inject

class SearchSectionsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(query: String): Result<List<Section>> {
        return searchRepository.search(query)
    }
}
