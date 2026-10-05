package com.example.musicplayer.data.model

data class SavedPlaylist(
    val id: String,
    val title: String,
    val description: String = "",
    val isDefault: Boolean = false
)
