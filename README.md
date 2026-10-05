# YouTube Music Player for Android

A modern Android application rewritten in Kotlin and Jetpack Compose from the original YouTube Music Player web app.

## Features

- **YouTube Playlist Streaming**: Seamlessly loads YouTube playlists by ID or URL (e.g., standard playlists, channel uploads `UU...`, mixes).
- **Faithful Glassmorphic UI**: Recreates the dark teal gradient styling (`#0F2027` to `#203A43` to `#2C5364`) and translucent glass card aesthetic.
- **Spinning Vinyl / Album Art**: Features an animated circular album art that rotates smoothly during playback and pauses in place when paused.
- **Full Playback Controls**: Previous track, Next track, large Spotify/YouTube green Play/Pause toggle, and interactive playback scrub bar with live timestamps.
- **Interactive Playlist & Track Queue**: Displays available tracks with active track highlight (`NOW` badge and glowing green highlight) and direct track jumping.
- **Preset Quick Playlists**: Quick chips for default channel uploads, Malayalam Top Hits, Lo-Fi Chill beats, and Global Pop.
- **Malayalam & English Localization**: Malayalam UI strings matching the original web app.
- **Persistent Preferences**: Remembers the last loaded playlist and playback history across sessions.
- **Custom Adaptive Icon**: Custom designed adaptive launcher icon with density variants.

## Tech Stack

- **Platform**: Android SDK 36, Kotlin 2.1.0, JDK 21
- **UI Framework**: Jetpack Compose with Material Design 3
- **Image Loading**: Coil Compose
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Playback Engine**: YouTube IFrame Player API via Android WebKit bridge
