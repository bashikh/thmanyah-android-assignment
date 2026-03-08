package com.thmanyah.shasha.di

import com.thmanyah.shasha.data.repository.HomeRepositoryImpl
import com.thmanyah.shasha.data.repository.SearchRepositoryImpl
import com.thmanyah.shasha.domain.repository.HomeRepository
import com.thmanyah.shasha.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}
