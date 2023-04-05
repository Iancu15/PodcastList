package com.podcastlist.api.model

data class SubscribedPodcasts (
    val items: List<SubscribedPodcastsItem> = arrayListOf()
)

data class SubscribedPodcastsItem (
    val show: SubscribedPodcast
)

data class SubscribedPodcast (
    val name: String,
    val id: String,
    val publisher: String,
    val images: List<PodcastImage>
    )

