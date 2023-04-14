package com.podcastlist.api.model

data class EpisodesQuery(
    val items: List<PodcastEpisode> = arrayListOf()
)

data class PodcastEpisode(
    val name: String,
    val uri: String,
    val description: String,
    val duration_ms: Long,
    val release_date: String,
    val id: String,
    val resume_point: ResumePoint
)

data class ResumePoint(
    val fully_played: Boolean,
    val resume_position_ms: Long
)