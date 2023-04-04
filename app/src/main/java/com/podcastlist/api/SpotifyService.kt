package com.podcastlist.api

import com.podcastlist.api.model.SubscribedPodcasts
import retrofit2.http.*


interface SpotifyService {
    @GET("/v1/me/shows")
    suspend fun getSubscribedPodcasts(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ) : SubscribedPodcasts

}