package com.podcastlist.api

interface AuthorizationService {
    var authorizationToken: String
    var isTokenAvailable: Boolean
}