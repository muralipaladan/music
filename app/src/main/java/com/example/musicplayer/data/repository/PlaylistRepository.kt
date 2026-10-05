package com.example.musicplayer.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.musicplayer.data.model.SavedPlaylist

class PlaylistRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("music_player_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SAVED_PLAYLIST = "mySavedPlaylist"
        const val KEY_PLAYLIST_HISTORY = "playlist_history"
        const val DEFAULT_PLAYLIST_ID = "UU8NeSRg0bBs_RnqVxL62B6w"
    }

    val defaultPlaylists = listOf(
        SavedPlaylist(
            id = DEFAULT_PLAYLIST_ID,
            title = "Default Hits Playlist",
            description = "Channel Uploads & Popular Tracks",
            isDefault = true
        ),
        SavedPlaylist(
            id = "PL4fGSIndQ80V87x71mKTh-Dph19a_Wd66",
            title = "Malayalam Top Hits",
            description = "Superhit Malayalam Songs",
            isDefault = false
        ),
        SavedPlaylist(
            id = "PLDISKgcnTR4w3cQ1S2O_6_u1k6u0N114L",
            title = "Lofi Chill & Beats",
            description = "Relaxing Study & Focus Music",
            isDefault = false
        ),
        SavedPlaylist(
            id = "RDCLAK5uy_kfdmRP1CPG4g_VbVj9wZ_7g-4Wp5_pB_4",
            title = "Global Pop Melodies",
            description = "Trending Worldwide Tunes",
            isDefault = false
        )
    )

    fun getSavedPlaylistId(): String {
        return prefs.getString(KEY_SAVED_PLAYLIST, DEFAULT_PLAYLIST_ID) ?: DEFAULT_PLAYLIST_ID
    }

    fun savePlaylistId(playlistId: String) {
        prefs.edit().putString(KEY_SAVED_PLAYLIST, playlistId).apply()
        addToHistory(playlistId)
    }

    fun getPlaylistHistory(): List<String> {
        val raw = prefs.getString(KEY_PLAYLIST_HISTORY, "") ?: ""
        if (raw.isEmpty()) return listOf(DEFAULT_PLAYLIST_ID)
        return raw.split(",").filter { it.isNotBlank() }
    }

    private fun addToHistory(playlistId: String) {
        val current = getPlaylistHistory().toMutableList()
        current.remove(playlistId)
        current.add(0, playlistId)
        val trimmed = current.take(15)
        prefs.edit().putString(KEY_PLAYLIST_HISTORY, trimmed.joinToString(",")).apply()
    }

    fun parsePlaylistId(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        return when {
            trimmed.contains("list=") -> {
                val afterList = trimmed.substringAfter("list=")
                afterList.substringBefore("&").substringBefore("#")
            }
            trimmed.contains("youtu.be/") -> {
                trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
            }
            else -> trimmed
        }
    }
}
