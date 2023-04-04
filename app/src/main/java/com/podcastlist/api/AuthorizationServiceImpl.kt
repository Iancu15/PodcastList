package com.podcastlist.api

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationServiceImpl @Inject constructor(
) : AuthorizationService {
    override var authorizationToken: String = ""
    override var isTokenAvailable: Boolean = false
}