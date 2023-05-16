package com.podcastlist.module

import com.podcastlist.api.AuthorizationService
import com.podcastlist.api.AuthorizationServiceImpl
import com.podcastlist.auth.AccountService
import com.podcastlist.auth.AccountServiceImpl
import com.podcastlist.db.DatabaseService
import com.podcastlist.db.DatabaseServiceImpl
import com.podcastlist.storage.PersistentStorage
import com.podcastlist.storage.Storage
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

    @Binds
    abstract fun provideDatabaseService(impl: DatabaseServiceImpl): DatabaseService

    @Binds
    abstract fun provideStorage(impl: PersistentStorage): Storage
}