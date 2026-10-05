package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.SubjectCategory
import com.example.musicplayer.data.model.SubjectPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SubjectPlaylistRepository {

    val categories: List<SubjectCategory> = listOf(
        SubjectCategory(
            id = "malayalam_hits",
            titleMalayalam = "മലയാളം ഹിറ്റുകൾ",
            titleEnglish = "Malayalam Top Hits",
            iconName = "star",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PL4fGSIndQ80V87x71mKTh-Dph19a_Wd66",
                    title = "Malayalam Trending 2024",
                    description = "Chartbusting new Malayalam releases & viral tracks",
                    subjectId = "malayalam_hits",
                    songCount = 30,
                    thumbnailUrl = "https://img.youtube.com/vi/a3Ue-LN5B9U/hqdefault.jpg"
                ),
                SubjectPlaylist(
                    id = "RDCLAK5uy_kfdmRP1CPG4g_VbVj9wZ_7g-4Wp5_pB_4",
                    title = "Evergreen Malayalam Favourites",
                    description = "Timeless classics and popular radio hits",
                    subjectId = "malayalam_hits",
                    songCount = 45,
                    thumbnailUrl = "https://img.youtube.com/vi/bXjO_r9-i2I/hqdefault.jpg"
                ),
                SubjectPlaylist(
                    id = "PLb2EvW5YmXmE8p6a8xV06N42d997ZkM1y",
                    title = "Sushin Shyam & New Wave",
                    description = "Electrifying Malayalam OSTs and background scores",
                    subjectId = "malayalam_hits",
                    songCount = 28,
                    thumbnailUrl = "https://img.youtube.com/vi/e12dO-9Y-vY/hqdefault.jpg"
                )
            )
        ),
        SubjectCategory(
            id = "melodies",
            titleMalayalam = "പ്രണയഗാനങ്ങൾ",
            titleEnglish = "Romantic Melodies",
            iconName = "favorite",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PLFgquLnL59am7VvX_Q1KqgDvxK20R8q7x",
                    title = "Soulful Malayalam Romance",
                    description = "Heart-touching romantic melodies and soft tunes",
                    subjectId = "melodies",
                    songCount = 35,
                    thumbnailUrl = "https://img.youtube.com/vi/yJg-Y5byMMw/hqdefault.jpg"
                ),
                SubjectPlaylist(
                    id = "PL4fGSIndQ80Wj2T3o3sFq37jP07xS3g5q",
                    title = "Rain & Coffee Melodies",
                    description = "Acoustic and soothing songs for gentle evenings",
                    subjectId = "melodies",
                    songCount = 25,
                    thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg"
                )
            )
        ),
        SubjectCategory(
            id = "devotional",
            titleMalayalam = "ഭക്തിഗാനങ്ങൾ",
            titleEnglish = "Devotional Songs",
            iconName = "temple",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PL6k9nN3E1c2i0H9r-8y3j_m6k_p1z2q3w",
                    title = "Ayyappa & Hindu Bhakthi Ganam",
                    description = "Yesudas & Chithra devotional masterpieces",
                    subjectId = "devotional",
                    songCount = 40,
                    thumbnailUrl = "https://img.youtube.com/vi/8Z7xL9w0e2Q/hqdefault.jpg"
                ),
                SubjectPlaylist(
                    id = "PL6k9nN3E1c2i0H9r-ChristianDevotional",
                    title = "Christian Devotional Melodies",
                    description = "Peaceful prayer songs and choir hymns",
                    subjectId = "devotional",
                    songCount = 30,
                    thumbnailUrl = "https://img.youtube.com/vi/2v8y8k_xL8Q/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "malayalam christian devotional songs hits"
                ),
                SubjectPlaylist(
                    id = "PL6k9nN3E1c2i0H9r-MappilaPattu",
                    title = "Mappila Pattu & Islamic Songs",
                    description = "Traditional Malabar Mappila songs and Madh songs",
                    subjectId = "devotional",
                    songCount = 30,
                    thumbnailUrl = "https://img.youtube.com/vi/3w0e8xL2k8Q/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "malayalam mappila songs super hits"
                )
            )
        ),
        SubjectCategory(
            id = "old_classics",
            titleMalayalam = "പഴയ സുവർണ്ണ ഗാനങ്ങൾ",
            titleEnglish = "Old Golden Classics",
            iconName = "history",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PL4fGSIndQ80XyZ1_80s90sGoldenHits",
                    title = "70s, 80s & 90s Golden Melodies",
                    description = "Yesudas, S. Janaki, Chithra, M.G. Sreekumar",
                    subjectId = "old_classics",
                    songCount = 50,
                    thumbnailUrl = "https://img.youtube.com/vi/w7x2L8k9p3Q/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "malayalam old golden hits yesudas chithra"
                ),
                SubjectPlaylist(
                    id = "PL4fGSIndQ80XyZ2_JohnsonMasterHits",
                    title = "Johnson Master & Baburaj Classics",
                    description = "Pure nostalgic violin harmonies and evergreen chords",
                    subjectId = "old_classics",
                    songCount = 35,
                    thumbnailUrl = "https://img.youtube.com/vi/5y9x8L2k1wQ/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "johnson master evergreen malayalam hits"
                )
            )
        ),
        SubjectCategory(
            id = "party_dance",
            titleMalayalam = "അടിപൊളി ഡാൻസ്",
            titleEnglish = "Party & Fast Beats",
            iconName = "celebration",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PL4fGSIndQ80PartyFastMalayalam",
                    title = "Kerala Festival & Onam Beats",
                    description = "Fast dance numbers, Shinkari Melam & club mixes",
                    subjectId = "party_dance",
                    songCount = 30,
                    thumbnailUrl = "https://img.youtube.com/vi/9x8L2k3w1Q0/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "malayalam party dance fast songs super hits"
                ),
                SubjectPlaylist(
                    id = "PL4fGSIndQ80FolkNadannaPattu",
                    title = "Nadanna Pattukal (Folk Songs)",
                    description = "Authentic Kerala folk rhythms & Kalabhavan Mani hits",
                    subjectId = "party_dance",
                    songCount = 25,
                    thumbnailUrl = "https://img.youtube.com/vi/2L8k9w0x3Q1/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "kalabhavan mani nadan pattukal super hits"
                )
            )
        ),
        SubjectCategory(
            id = "lofi_chill",
            titleMalayalam = "ലോഫൈ & റിലാക്സ്",
            titleEnglish = "Lofi & Chill Beats",
            iconName = "headphones",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PLDISKgcnTR4w3cQ1S2O_6_u1k6u0N114L",
                    title = "Lofi Midnight Beats",
                    description = "Lo-Fi hip hop beats to relax/study to",
                    subjectId = "lofi_chill",
                    songCount = 40,
                    thumbnailUrl = "https://img.youtube.com/vi/jfKfPfyJRdk/hqdefault.jpg"
                ),
                SubjectPlaylist(
                    id = "PLDISKgcnTR4MalayalamLofiChill",
                    title = "Malayalam Lofi Slowed & Reverb",
                    description = "Aesthetic chill versions of popular Malayalam tunes",
                    subjectId = "lofi_chill",
                    songCount = 30,
                    thumbnailUrl = "https://img.youtube.com/vi/e12dO-9Y-vY/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "malayalam lofi chill songs slowed reverb"
                )
            )
        ),
        SubjectCategory(
            id = "workout_energy",
            titleMalayalam = "വർക്കൗട്ട് & എനർജി",
            titleEnglish = "Workout & Motivation",
            iconName = "fitness",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PLWorkoutMotivationEnergyBeats",
                    title = "Gym Workout High Energy Beats",
                    description = "Intense cardio and bodybuilding motivational tracks",
                    subjectId = "workout_energy",
                    songCount = 35,
                    thumbnailUrl = "https://img.youtube.com/vi/2zToEPpFEN8/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "gym workout music motivational beats"
                )
            )
        ),
        SubjectCategory(
            id = "instrumental",
            titleMalayalam = "ഇൻസ്ട്രുമെന്റൽ & ധ്യാനം",
            titleEnglish = "Instrumental & Meditation",
            iconName = "piano",
            playlists = listOf(
                SubjectPlaylist(
                    id = "PLInstrumentalFluteViolinRelax",
                    title = "Indian Bamboo Flute & Violin Melodies",
                    description = "Peaceful morning meditation & stress relief",
                    subjectId = "instrumental",
                    songCount = 25,
                    thumbnailUrl = "https://img.youtube.com/vi/DWcJFNfaw9c/hqdefault.jpg",
                    isSearchQuery = true,
                    searchQuery = "indian classical flute violin instrumental peaceful"
                )
            )
        )
    )

    fun searchPlaylists(query: String): List<SubjectPlaylist> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val results = mutableListOf<SubjectPlaylist>()
        for (cat in categories) {
            for (p in cat.playlists) {
                if (p.title.lowercase().contains(q) ||
                    p.description.lowercase().contains(q) ||
                    cat.titleMalayalam.lowercase().contains(q) ||
                    cat.titleEnglish.lowercase().contains(q)
                ) {
                    results.add(p)
                }
            }
        }

        // Always add a dynamic YouTube Search Playlist option matching the user's subject query!
        results.add(
            SubjectPlaylist(
                id = "search_$q",
                title = "യൂട്യൂബിൽ: \"$query\"",
                description = "YouTube-ൽ ഈ വിഷയത്തിലുള്ള ഏറ്റവും പുതിയ ഗാനങ്ങൾ പ്ലേ ചെയ്യുക",
                subjectId = "custom_search",
                songCount = 50,
                thumbnailUrl = "https://via.placeholder.com/200/1e1e1e/1db954?text=${query.take(5)}",
                isSearchQuery = true,
                searchQuery = query
            )
        )
        return results
    }

    suspend fun fetchVideoTitle(videoId: String): String = withContext(Dispatchers.IO) {
        if (videoId.isBlank()) return@withContext ""
        try {
            val endpoint = "https://noembed.com/embed?url=https://www.youtube.com/watch?v=$videoId"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                return@withContext json.optString("title", "")
            }
        } catch (_: Exception) { }
        return@withContext ""
    }
}
