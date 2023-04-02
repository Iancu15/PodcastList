package com.podcastlist.api

interface AuthorizationService {
    var authorizationToken: String
    suspend fun refreshAccessToken()
}