package com.podcastlist.storage.model

import com.podcastlist.api.model.PodcastEpisode

data class EpisodesList(
    val pageNumber: Int,
    val podcastId: String,
    val episodes: List<PodcastEpisode>
)