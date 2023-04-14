package com.podcastlist.api.model

data class EpisodesQuery(
    val items: List<Episode> = arrayListOf()
)

data class Episode(
    val name: String,
    val uri: String,
    val description: String,
    val duration_ms: Int,
    val release_date: String,
    val id: String
)
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
    val images: List<PodcastImage>
    )