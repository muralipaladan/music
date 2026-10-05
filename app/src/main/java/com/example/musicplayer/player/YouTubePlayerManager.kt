package com.example.musicplayer.player

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class YouTubePlayerManager(
    private val context: Context,
    private val listener: YouTubePlayerListener
) {
    private var webView: WebView? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isInitialized = false

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize(initialPlaylistId: String): WebView {
        if (webView != null) return webView!!

        val view = WebView(context.applicationContext).apply {
            setBackgroundColor(Color.TRANSPARENT)
            visibility = View.VISIBLE
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
            }
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }
            addJavascriptInterface(YouTubeBridge(listener), "AndroidBridge")
        }

        webView = view
        loadHtml(initialPlaylistId)
        isInitialized = true
        return view
    }

    private fun loadHtml(playlistId: String) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
                <style>
                    body, html { margin:0; padding:0; background: #0f2027; overflow: hidden; width:100%; height:100%; }
                    #player { width: 100%; height: 100%; }
                </style>
            </head>
            <body>
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                    var player;
                    var currentPlaylistId = '$playlistId';
                    var isReady = false;

                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            playerVars: {
                                'listType': 'playlist',
                                'list': currentPlaylistId,
                                'autoplay': 1,
                                'controls': 1,
                                'playsinline': 1,
                                'rel': 0,
                                'enablejsapi': 1,
                                'origin': 'https://www.youtube.com'
                            },
                            events: {
                                'onReady': onPlayerReady,
                                'onStateChange': onPlayerStateChange,
                                'onError': onPlayerError
                            }
                        });
                    }

                    function onPlayerReady(event) {
                        isReady = true;
                        if (window.AndroidBridge) {
                            window.AndroidBridge.onReady();
                        }
                        setTimeout(fetchTracks, 1500);
                    }

                    function onPlayerStateChange(event) {
                        if (window.AndroidBridge) {
                            window.AndroidBridge.onStateChange(event.data);
                        }
                        if (event.data === 1) { // PLAYING
                            updateVideoDetails();
                            fetchTracks();
                        }
                    }

                    function onPlayerError(event) {
                        if (window.AndroidBridge) {
                            window.AndroidBridge.onError(event.data);
                        }
                        setTimeout(nextVideo, 2000);
                    }

                    function updateVideoDetails() {
                        if (player && typeof player.getVideoData === 'function') {
                            var data = player.getVideoData();
                            var index = 0;
                            try {
                                if (typeof player.getPlaylistIndex === 'function') {
                                    index = player.getPlaylistIndex();
                                }
                            } catch(e) {}
                            var duration = 0;
                            var currentTime = 0;
                            try {
                                if (typeof player.getDuration === 'function') duration = player.getDuration();
                                if (typeof player.getCurrentTime === 'function') currentTime = player.getCurrentTime();
                            } catch(e) {}

                            if (data && window.AndroidBridge) {
                                window.AndroidBridge.onTrackDetails(
                                    data.title || "",
                                    data.author || "YouTube Music",
                                    data.video_id || "",
                                    duration,
                                    currentTime,
                                    index
                                );
                            }
                        }
                    }

                    function fetchTracks() {
                        if (player && typeof player.getPlaylist === 'function') {
                            var list = player.getPlaylist();
                            if (list && list.length > 0 && window.AndroidBridge) {
                                window.AndroidBridge.onPlaylistLoaded(JSON.stringify(list));
                            }
                        }
                    }

                    function playVideo() {
                        if (player && typeof player.playVideo === 'function') {
                            player.playVideo();
                        }
                    }

                    function pauseVideo() {
                        if (player && typeof player.pauseVideo === 'function') {
                            player.pauseVideo();
                        }
                    }

                    function nextVideo() {
                        if (player && typeof player.nextVideo === 'function') {
                            player.nextVideo();
                        }
                    }

                    function prevVideo() {
                        if (player && typeof player.previousVideo === 'function') {
                            player.previousVideo();
                        }
                    }

                    function playTrackAt(idx) {
                        if (player && typeof player.playVideoAt === 'function') {
                            player.playVideoAt(idx);
                        }
                    }

                    function loadVideoById(vid) {
                        if (player && typeof player.loadVideoById === 'function') {
                            player.loadVideoById(vid);
                        }
                    }

                    function seekToSec(sec) {
                        if (player && typeof player.seekTo === 'function') {
                            player.seekTo(sec, true);
                        }
                    }

                    function loadNewPlaylist(id) {
                        currentPlaylistId = id;
                        if (player && typeof player.loadPlaylist === 'function') {
                            player.loadPlaylist({
                                'list': id,
                                'listType': 'playlist',
                                'index': 0
                            });
                            setTimeout(fetchTracks, 2000);
                        }
                    }

                    function loadSearchQuery(query) {
                        if (player && typeof player.loadPlaylist === 'function') {
                            player.loadPlaylist({
                                'list': query,
                                'listType': 'search',
                                'index': 0
                            });
                            setTimeout(fetchTracks, 2000);
                        }
                    }

                    setInterval(function() {
                        if (player && isReady && typeof player.getPlayerState === 'function') {
                            try {
                                if (player.getPlayerState() === 1) {
                                    var cur = player.getCurrentTime ? player.getCurrentTime() : 0;
                                    var dur = player.getDuration ? player.getDuration() : 0;
                                    if (window.AndroidBridge) {
                                        window.AndroidBridge.onPlaybackProgress(cur, dur);
                                    }
                                }
                            } catch(e) {}
                        }
                    }, 1000);
                </script>
            </body>
            </html>
        """.trimIndent()

        mainHandler.post {
            webView?.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
        }
    }

    fun play() {
        evaluateJs("playVideo();")
    }

    fun pause() {
        evaluateJs("pauseVideo();")
    }

    fun next() {
        evaluateJs("nextVideo();")
    }

    fun previous() {
        evaluateJs("prevVideo();")
    }

    fun playTrackAt(index: Int) {
        evaluateJs("playTrackAt($index);")
    }

    fun playVideoById(videoId: String) {
        evaluateJs("loadVideoById('$videoId');")
    }

    fun seekTo(seconds: Float) {
        evaluateJs("seekToSec($seconds);")
    }

    fun loadPlaylist(playlistId: String) {
        evaluateJs("loadNewPlaylist('$playlistId');")
    }

    fun searchAndPlay(query: String) {
        val safeQuery = query.replace("'", "\\'").replace("\"", "\\\"")
        evaluateJs("loadSearchQuery('$safeQuery');")
    }

    private fun evaluateJs(script: String) {
        mainHandler.post {
            webView?.evaluateJavascript(script, null)
        }
    }

    fun release() {
        mainHandler.post {
            webView?.destroy()
            webView = null
        }
    }
}
