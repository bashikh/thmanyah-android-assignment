package com.thmanyah.shasha.data.remote

import com.thmanyah.shasha.data.remote.dto.HomeSectionsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("search")
    suspend fun search(@Query("q") query: String): HomeSectionsResponseDto
}
