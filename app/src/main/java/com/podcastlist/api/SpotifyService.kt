package com.podcastlist.api

import com.podcastlist.api.model.Podcasts
import com.podcastlist.api.model.SpotifyQuery
import retrofit2.http.*


interface SpotifyService {
    @GET("/v1/me/shows")
    suspend fun getSubscribedPodcasts(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ) : Podcasts

    @PUT("/v1/me/shows")
    suspend fun subscribeToPodcasts(
        @Query("ids") ids: String,
        @Header("Authorization") authorization: String
    )

    @GET("/v1/search")
    suspend fun searchPodcasts(
        @Query("q") searchQuery: String,
        @Query("type") types: List<String> = arrayListOf("show"),
        @Query("limit") limit: Int = 9,
        @Header("Authorization") authorization: String
    ) : SpotifyQuery

    @DELETE("/v1/me/shows")
    suspend fun unsubscribeFromPodcasts(
        @Query("ids") ids: String,
        @Header("Authorization") authorization: String
    )

}