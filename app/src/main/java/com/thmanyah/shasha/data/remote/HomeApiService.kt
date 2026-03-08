package com.thmanyah.shasha.data.remote

import com.thmanyah.shasha.data.remote.dto.HomeSectionsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApiService {
    @GET("home_sections")
    suspend fun getHomeSections(@Query("page") page: Int = 1): HomeSectionsResponseDto
}
