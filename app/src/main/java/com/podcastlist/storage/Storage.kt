package com.podcastlist.storage

import com.podcastlist.storage.model.EpisodesList
import kotlinx.coroutines.flow.Flow

interface Storage {
    suspend fun add(data: EpisodesList)

    fun get(podcastId: String, pageNumber: Int): Flow<EpisodesList>
}