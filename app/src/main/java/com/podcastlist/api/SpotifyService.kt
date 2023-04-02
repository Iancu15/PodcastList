package com.podcastlist.api

import AuthorizationToken
import com.podcastlist.api.model.SubscribedPodcasts
import retrofit2.Call
import retrofit2.http.*


interface SpotifyService {

    @FormUrlEncoded
    @POST
    suspend fun getAccessToken(
        @Url url: String,
        @Field("grant_type") grantType: String = GRANT_TYPE,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET,
        @Field("redirect_uri") redirectUri: String = REDICT_URI,
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded"
    ) : AuthorizationToken

    @GET("/v1/me/shows")
    suspend fun getSubscribedPodcasts(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ) : SubscribedPodcasts

}