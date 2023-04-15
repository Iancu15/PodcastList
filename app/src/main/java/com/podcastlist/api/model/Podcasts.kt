package com.podcastlist.api.model
data class SpotifyQuery(
    val shows: Shows
)

data class Shows(
    val items: List<Podcast>
)
data class Podcasts(
    var items: List<PodcastsItem> = arrayListOf()
)

data class PodcastsItem (
    val show: Podcast
)

data class Podcast (
    val name: String,
    val id: String,
    val publisher: String,
    val images: List<PodcastImage>,
    val description: String
    )