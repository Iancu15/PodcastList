package com.podcastlist.module

import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.AuthorizationServiceImpl
import com.podcastlist.auth.AccountService
import com.podcastlist.auth.AccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideAuthorizationService(impl: AuthorizationServiceImpl): AuthorizationService
}