package com.mavalore.tricenari.di

import com.mavalore.tricenari.data.repository.RepositoryImpl
import com.mavalore.tricenari.domain.interfaces.Repository
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
    abstract fun bindRepository(
        repositoryImpl:RepositoryImpl
    ):Repository

}