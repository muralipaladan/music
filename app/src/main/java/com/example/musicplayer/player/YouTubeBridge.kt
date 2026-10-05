package com.example.musicplayer.player

import android.webkit.JavascriptInterface

interface YouTubePlayerListener {
    fun onPlayerReady()
    fun onPlayerStateChange(state: Int)
    fun onTrackDetails(title: String, artist: String, videoId: String, duration: Float, currentTime: Float, index: Int)
    fun onPlaylistLoaded(trackVideoIds: List<String>)
    fun onPlaybackProgress(currentTime: Float, duration: Float)
    fun onPlayerError(errorCode: Int)
}

class YouTubeBridge(private val listener: YouTubePlayerListener) {

    @JavascriptInterface
    fun onReady() {
        listener.onPlayerReady()
    }

    @JavascriptInterface
    fun onStateChange(state: Int) {
        listener.onPlayerStateChange(state)
    }

    @JavascriptInterface
    fun onTrackDetails(
        title: String,
        artist: String,
        videoId: String,
        duration: Float,
        currentTime: Float,
        index: Int
    ) {
        listener.onTrackDetails(title, artist, videoId, duration, currentTime, index)
    }

    @JavascriptInterface
    fun onPlaylistLoaded(playlistJson: String) {
        // Parse simple json array of strings: ["id1", "id2", ...]
        val ids = mutableListOf<String>()
        try {
            val clean = playlistJson.trim().removeSurrounding("[", "]")
            if (clean.isNotEmpty()) {
                val tokens = clean.split(",")
                for (token in tokens) {
                    val id = token.trim().removeSurrounding("\"").removeSurrounding("'")
                    if (id.isNotEmpty()) ids.add(id)
                }
            }
        } catch (_: Exception) { }
        listener.onPlaylistLoaded(ids)
    }

    @JavascriptInterface
    fun onPlaybackProgress(currentTime: Float, duration: Float) {
        listener.onPlaybackProgress(currentTime, duration)
    }

    @JavascriptInterface
    fun onError(errorCode: Int) {
        listener.onPlayerError(errorCode)
    }
}
