package com.example.musicplayer.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.musicplayer.data.model.MusicTrack

class IndexedSongsDatabase(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "indexed_music_library.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_SONGS = "indexed_songs"
        private const val COL_VIDEO_ID = "video_id"
        private const val COL_TITLE = "title"
        private const val COL_ARTIST = "artist"
        private const val COL_DURATION = "duration_seconds"
        private const val COL_THUMBNAIL = "thumbnail_url"
        private const val COL_LAST_PLAYED = "last_played_time"
        private const val COL_PLAY_COUNT = "play_count"
        private const val COL_SOURCE_PLAYLIST = "source_playlist_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_SONGS (
                $COL_VIDEO_ID TEXT PRIMARY KEY,
                $COL_TITLE TEXT NOT NULL,
                $COL_ARTIST TEXT,
                $COL_DURATION REAL DEFAULT 0,
                $COL_THUMBNAIL TEXT,
                $COL_LAST_PLAYED INTEGER DEFAULT 0,
                $COL_PLAY_COUNT INTEGER DEFAULT 0,
                $COL_SOURCE_PLAYLIST TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Create indexes for blazing fast search and sorting
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_last_played ON $TABLE_SONGS ($COL_LAST_PLAYED DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_title ON $TABLE_SONGS ($COL_TITLE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_play_count ON $TABLE_SONGS ($COL_PLAY_COUNT DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }

    fun insertOrUpdateSong(track: MusicTrack, sourcePlaylistId: String? = null) {
        if (track.videoId.isBlank()) return
        val db = writableDatabase
        try {
            val cv = ContentValues().apply {
                put(COL_VIDEO_ID, track.videoId)
                put(COL_TITLE, track.title)
                put(COL_ARTIST, track.artist)
                put(COL_DURATION, track.durationSeconds)
                put(COL_THUMBNAIL, track.thumbnailUrl)
                if (sourcePlaylistId != null) {
                    put(COL_SOURCE_PLAYLIST, sourcePlaylistId)
                }
            }
            db.insertWithOnConflict(TABLE_SONGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        } catch (_: Exception) { }
    }

    fun insertOrUpdateBatch(tracks: List<MusicTrack>, sourcePlaylistId: String? = null) {
        if (tracks.isEmpty()) return
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (track in tracks) {
                if (track.videoId.isNotBlank()) {
                    val cv = ContentValues().apply {
                        put(COL_VIDEO_ID, track.videoId)
                        put(COL_TITLE, track.title)
                        put(COL_ARTIST, track.artist)
                        put(COL_DURATION, track.durationSeconds)
                        put(COL_THUMBNAIL, track.thumbnailUrl)
                        if (sourcePlaylistId != null) {
                            put(COL_SOURCE_PLAYLIST, sourcePlaylistId)
                        }
                    }
                    db.insertWithOnConflict(TABLE_SONGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                }
            }
            db.setTransactionSuccessful()
        } catch (_: Exception) {
        } finally {
            db.endTransaction()
        }
    }

    fun recordSongPlayed(videoId: String, title: String? = null, artist: String? = null) {
        if (videoId.isBlank()) return
        val db = writableDatabase
        try {
            val now = System.currentTimeMillis()
            val query = """
                INSERT INTO $TABLE_SONGS ($COL_VIDEO_ID, $COL_TITLE, $COL_ARTIST, $COL_LAST_PLAYED, $COL_PLAY_COUNT)
                VALUES (?, ?, ?, ?, 1)
                ON CONFLICT($COL_VIDEO_ID) DO UPDATE SET
                    $COL_LAST_PLAYED = $now,
                    $COL_PLAY_COUNT = $COL_PLAY_COUNT + 1,
                    $COL_TITLE = COALESCE(?, $COL_TITLE),
                    $COL_ARTIST = COALESCE(?, $COL_ARTIST)
            """.trimIndent()
            db.execSQL(query, arrayOf(videoId, title ?: "Unknown Track", artist ?: "YouTube", now, title, artist))
        } catch (_: Exception) {
            try {
                val cv = ContentValues().apply {
                    put(COL_LAST_PLAYED, System.currentTimeMillis())
                }
                db.update(TABLE_SONGS, cv, "$COL_VIDEO_ID = ?", arrayOf(videoId))
            } catch (_: Exception) {}
        }
    }

    fun getCachedSong(videoId: String): MusicTrack? {
        if (videoId.isBlank()) return null
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_VIDEO_ID, $COL_TITLE, $COL_ARTIST, $COL_DURATION FROM $TABLE_SONGS WHERE $COL_VIDEO_ID = ? LIMIT 1",
            arrayOf(videoId)
        )
        cursor.use {
            if (it.moveToFirst()) {
                val vid = it.getString(0)
                val title = it.getString(1)
                val artist = it.getString(2)
                val dur = it.getFloat(3)
                return MusicTrack(
                    index = 0,
                    videoId = vid,
                    title = title,
                    artist = artist,
                    durationSeconds = dur
                )
            }
        }
        return null
    }

    fun getAllIndexedSongs(searchQuery: String? = null): List<MusicTrack> {
        val db = readableDatabase
        val songs = mutableListOf<MusicTrack>()
        val sql: String
        val args: Array<String>?

        if (!searchQuery.isNullOrBlank()) {
            sql = "SELECT $COL_VIDEO_ID, $COL_TITLE, $COL_ARTIST, $COL_DURATION FROM $TABLE_SONGS WHERE $COL_TITLE LIKE ? OR $COL_ARTIST LIKE ? ORDER BY $COL_LAST_PLAYED DESC, $COL_PLAY_COUNT DESC"
            val pattern = "%${searchQuery.trim()}%"
            args = arrayOf(pattern, pattern)
        } else {
            sql = "SELECT $COL_VIDEO_ID, $COL_TITLE, $COL_ARTIST, $COL_DURATION FROM $TABLE_SONGS ORDER BY $COL_LAST_PLAYED DESC, $COL_PLAY_COUNT DESC LIMIT 200"
            args = null
        }

        val cursor = db.rawQuery(sql, args)
        cursor.use {
            var idx = 0
            while (it.moveToNext()) {
                val vid = it.getString(0)
                val title = it.getString(1)
                val artist = it.getString(2)
                val dur = it.getFloat(3)
                songs.add(
                    MusicTrack(
                        index = idx++,
                        videoId = vid,
                        title = title,
                        artist = artist,
                        durationSeconds = dur
                    )
                )
            }
        }
        return songs
    }

    fun getRecentlyPlayed(limit: Int = 50): List<MusicTrack> {
        val db = readableDatabase
        val songs = mutableListOf<MusicTrack>()
        val cursor = db.rawQuery(
            "SELECT $COL_VIDEO_ID, $COL_TITLE, $COL_ARTIST, $COL_DURATION FROM $TABLE_SONGS WHERE $COL_LAST_PLAYED > 0 ORDER BY $COL_LAST_PLAYED DESC LIMIT ?",
            arrayOf(limit.toString())
        )
        cursor.use {
            var idx = 0
            while (it.moveToNext()) {
                val vid = it.getString(0)
                val title = it.getString(1)
                val artist = it.getString(2)
                val dur = it.getFloat(3)
                songs.add(
                    MusicTrack(
                        index = idx++,
                        videoId = vid,
                        title = title,
                        artist = artist,
                        durationSeconds = dur
                    )
                )
            }
        }
        return songs
    }
}
