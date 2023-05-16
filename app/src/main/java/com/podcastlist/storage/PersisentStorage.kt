package com.podcastlist.storage

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.podcastlist.storage.model.EpisodesList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val EMPTY_JSON_STRING = "[]"
class PersistentStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : Storage {
    private val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        .setLenient().create()
    override suspend fun add(data: EpisodesList) {
        val key = stringPreferencesKey(data.podcastId + data.pageNumber.toString())
        dataStore.edit { episodesLists ->
            episodesLists[key] = gson.toJson(data)
        }
    }

    override fun get(podcastId: String, pageNumber: Int): Flow<EpisodesList> {
        val key = stringPreferencesKey(podcastId + pageNumber.toString())
        return dataStore.data
            .map { preferences ->
                val jsonString = preferences[key] ?: EMPTY_JSON_STRING
                gson.fromJson(jsonString, EpisodesList::class.java)
            }
    }

}