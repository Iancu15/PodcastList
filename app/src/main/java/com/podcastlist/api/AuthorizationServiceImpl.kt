package com.podcastlist.api

import android.util.Log
import javax.inject.Inject

class AuthorizationServiceImpl @Inject constructor(
    private val spotifyService: SpotifyService
) : AuthorizationService {
    override lateinit var authorizationToken: String
    private var tokenAvailabilityTime: Long = 0L
    private var lastTimestamp: Long = 0L

    override suspend fun refreshAccessToken() {
        val currentTimestamp = System.currentTimeMillis()/1000
        if (tokenAvailabilityTime == 0L || (currentTimestamp - lastTimestamp < tokenAvailabilityTime)) {
            lastTimestamp = currentTimestamp
            val authToken = spotifyService.getAccessToken(
                url = TOKEN_URL,
            )

            val accessToken = authToken.access_token
            val tokenType = authToken.token_type
            authorizationToken = "Authorization: $tokenType $accessToken"
            tokenAvailabilityTime = authToken.expires_in.toLong()
            Log.d("AuthorizationServiceImpl", "Refreshed access token")
            Log.d("AuthorizationServiceImpl", "Expires in $tokenAvailabilityTime seconds")
        }
    }

}