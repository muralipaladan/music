package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.MusicTrack
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
                    songCount = 6,
                    thumbnailUrl = "https://img.youtube.com/vi/a3Ue-LN5B9U/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "a3Ue-LN5B9U", "Illuminati (Aavesham)", "Sushin Shyam, Dabzee", 185f),
                        MusicTrack(1, "e12dO-9Y-vY", "Jaada (Aavesham)", "Sushin Shyam, Sreenath Bhasi", 210f),
                        MusicTrack(2, "bXjO_r9-i2I", "Kuthanthram (Manjummel Boys)", "Sushin Shyam, Vedan", 195f),
                        MusicTrack(3, "yJg-Y5byMMw", "Darshana (Hridayam)", "Hesham Abdul Wahab, Darshana", 240f),
                        MusicTrack(4, "kJQP7kiw5Fk", "Aaromal (Minnal Murali)", "Sushin Shyam, Sooraj Santhosh", 220f),
                        MusicTrack(5, "8Z7xL9w0e2Q", "Pala Palli Thiruppalli (Kaduva)", "Jakes Bejoy, Athul Narukara", 190f)
                    )
                ),
                SubjectPlaylist(
                    id = "RDCLAK5uy_kfdmRP1CPG4g_VbVj9wZ_7g-4Wp5_pB_4",
                    title = "Evergreen Malayalam Favourites",
                    description = "Timeless classics and popular radio hits",
                    subjectId = "malayalam_hits",
                    songCount = 5,
                    thumbnailUrl = "https://img.youtube.com/vi/bXjO_r9-i2I/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "yJg-Y5byMMw", "Darshana (Hridayam)", "Hesham Abdul Wahab", 240f),
                        MusicTrack(1, "w7x2L8k9p3Q", "Malare (Premam)", "Vijay Yesudas, Rajesh Murugesan", 255f),
                        MusicTrack(2, "2v8y8k_xL8Q", "Aaradhike (Ambili)", "Sooraj Santhosh, Madhuvanthi", 270f),
                        MusicTrack(3, "3w0e8xL2k8Q", "Uyiril Thodum (Kumbalangi Nights)", "Sooraj Santhosh, Anne Amie", 230f),
                        MusicTrack(4, "5y9x8L2k1wQ", "Mizhiyil Ninnum (Mayanadhi)", "Shahabaz Aman, Rex Vijayan", 260f)
                    )
                ),
                SubjectPlaylist(
                    id = "PLb2EvW5YmXmE8p6a8xV06N42d997ZkM1y",
                    title = "Sushin Shyam Musical Waves",
                    description = "Electrifying Malayalam OSTs and background scores",
                    subjectId = "malayalam_hits",
                    songCount = 4,
                    thumbnailUrl = "https://img.youtube.com/vi/e12dO-9Y-vY/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "a3Ue-LN5B9U", "Illuminati - Sushin Shyam", "Dabzee, Sushin Shyam", 185f),
                        MusicTrack(1, "bXjO_r9-i2I", "Kuthanthram - Manjummel Boys", "Sushin Shyam, Vedan", 195f),
                        MusicTrack(2, "e12dO-9Y-vY", "Jaada - Aavesham OST", "Sushin Shyam", 210f),
                        MusicTrack(3, "kJQP7kiw5Fk", "Aaromal - Minnal Murali OST", "Sushin Shyam", 220f)
                    )
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
                    songCount = 5,
                    thumbnailUrl = "https://img.youtube.com/vi/yJg-Y5byMMw/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "yJg-Y5byMMw", "Darshana (Hridayam)", "Hesham Abdul Wahab", 240f),
                        MusicTrack(1, "2v8y8k_xL8Q", "Aaradhike (Ambili)", "Sooraj Santhosh", 270f),
                        MusicTrack(2, "3w0e8xL2k8Q", "Uyiril Thodum (Kumbalangi Nights)", "Sooraj Santhosh, Anne Amie", 230f),
                        MusicTrack(3, "w7x2L8k9p3Q", "Malare Ninne (Premam)", "Vijay Yesudas", 255f),
                        MusicTrack(4, "5y9x8L2k1wQ", "Mizhiyil Ninnum (Mayanadhi)", "Shahabaz Aman", 260f)
                    )
                ),
                SubjectPlaylist(
                    id = "PL4fGSIndQ80Wj2T3o3sFq37jP07xS3g5q",
                    title = "Rain & Coffee Melodies",
                    description = "Acoustic and soothing songs for gentle evenings",
                    subjectId = "melodies",
                    songCount = 4,
                    thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "kJQP7kiw5Fk", "Mazha Paadum (Acoustic)", "Various Artists", 220f),
                        MusicTrack(1, "yJg-Y5byMMw", "Darshana (Coffee Unplugged)", "Hesham Abdul Wahab", 240f),
                        MusicTrack(2, "5y9x8L2k1wQ", "Mizhiyil Ninnum (Acoustic Rain)", "Shahabaz Aman", 260f),
                        MusicTrack(3, "2v8y8k_xL8Q", "Aaradhike (Gentle Breeze)", "Sooraj Santhosh", 270f)
                    )
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
                    songCount = 4,
                    thumbnailUrl = "https://img.youtube.com/vi/8Z7xL9w0e2Q/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "DWcJFNfaw9c", "ഹരിവരാസനം (Harivarasanam)", "K.J. Yesudas", 310f),
                        MusicTrack(1, "2zToEPpFEN8", "താമരപ്പൂവിൽ വാഴും ദേവീ (Thamarapoovil)", "K.S. Chithra", 290f),
                        MusicTrack(2, "jfKfPfyJRdk", "സ്വാമി സംഗീതമാലപാനം", "K.J. Yesudas", 280f),
                        MusicTrack(3, "8Z7xL9w0e2Q", "ശരണമയ്യപ്പാ സ്വാമി ശരണമയ്യപ്പാ", "Madhu Balakrishnan", 275f)
                    )
                ),
                SubjectPlaylist(
                    id = "PL6k9nN3E1c2i0H9r-ChristianDevotional",
                    title = "Christian Devotional Melodies",
                    description = "Peaceful prayer songs and choir hymns",
                    subjectId = "devotional",
                    songCount = 3,
                    thumbnailUrl = "https://img.youtube.com/vi/2v8y8k_xL8Q/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "2v8y8k_xL8Q", "യേശുവേ എൻ രക്ഷകാ", "Christian Choir", 300f),
                        MusicTrack(1, "3w0e8xL2k8Q", "ദൈവമേ നിൻ സ്നേഹം", "K.J. Yesudas", 290f),
                        MusicTrack(2, "5y9x8L2k1wQ", "പരിശുദ്ധാത്മാവേ എന്നിൽ നിറയണമേ", "Fr. Shaji Thumpechirayil", 320f)
                    )
                ),
                SubjectPlaylist(
                    id = "PL6k9nN3E1c2i0H9r-MappilaPattu",
                    title = "Mappila Pattu & Islamic Songs",
                    description = "Traditional Malabar Mappila songs and Madh songs",
                    subjectId = "devotional",
                    songCount = 3,
                    thumbnailUrl = "https://img.youtube.com/vi/3w0e8xL2k8Q/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "3w0e8xL2k8Q", "മക്കത്തെ പൂഞ്ചോല", "M.S. Baburaj Hits", 260f),
                        MusicTrack(1, "2L8k9w0x3Q1", "സമയമാം രഥത്തിൽ", "Classic Malabar Harmonies", 280f),
                        MusicTrack(2, "e12dO-9Y-vY", "മൈലാഞ്ചി മൊഞ്ചുള്ള", "Peevi Brothers", 250f)
                    )
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
                    songCount = 4,
                    thumbnailUrl = "https://img.youtube.com/vi/w7x2L8k9p3Q/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "w7x2L8k9p3Q", "മാണിക്യ വീണയുമായെൻ", "K.J. Yesudas", 270f),
                        MusicTrack(1, "5y9x8L2k1wQ", "പ്രമദവനം വീണ്ടും", "K.J. Yesudas, Raveendran", 310f),
                        MusicTrack(2, "9x8L2k3w1Q0", "ശ്യാമമേഘമേ നീ യദുകുല", "K.S. Chithra", 290f),
                        MusicTrack(3, "2L8k9w0x3Q1", "ഉണ്ണീ വാവാവോ പൊന്നുണ്ണീ വാവോ", "K.S. Chithra", 260f)
                    )
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
                    title = "Kerala Festival & Fast Beats",
                    description = "Fast dance numbers, Shinkari Melam & club mixes",
                    subjectId = "party_dance",
                    songCount = 4,
                    thumbnailUrl = "https://img.youtube.com/vi/9x8L2k3w1Q0/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "a3Ue-LN5B9U", "Illuminati (Aavesham)", "Sushin Shyam, Dabzee", 185f),
                        MusicTrack(1, "2L8k9w0x3Q1", "എന്തിന്റെ കണ്ണാ ജിമിക്കി കമ്മൽ", "Shaan Rahman, Vineeth", 205f),
                        MusicTrack(2, "e12dO-9Y-vY", "കരിങ്കാളിയല്ലേ കരിനീലക്കണ്ണേ", "Kalamandalam Sivan", 230f),
                        MusicTrack(3, "8Z7xL9w0e2Q", "പാലപ്പള്ളി തിരുപ്പള്ളി", "Jakes Bejoy, Athul Narukara", 190f)
                    )
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
                    songCount = 3,
                    thumbnailUrl = "https://img.youtube.com/vi/jfKfPfyJRdk/hqdefault.jpg",
                    initialTracks = listOf(
                        MusicTrack(0, "jfKfPfyJRdk", "Lofi Midnight Study Beats", "Lofi Girl Records", 300f),
                        MusicTrack(1, "e12dO-9Y-vY", "Malayalam Chill Slowed & Reverb", "Malayalam Lofi Project", 240f),
                        MusicTrack(2, "yJg-Y5byMMw", "Darshana Lofi Aesthetic Remix", "Lofi Beats India", 260f)
                    )
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

        // Add a dynamic YouTube Search Playlist option matching user query
        results.add(
            SubjectPlaylist(
                id = "search_$q",
                title = "യൂട്യൂബിൽ: \"$query\"",
                description = "YouTube-ൽ ഈ വിഷയത്തിലുള്ള ഗാനങ്ങൾ പ്ലേ ചെയ്യുക",
                subjectId = "custom_search",
                songCount = 10,
                thumbnailUrl = "https://img.youtube.com/vi/a3Ue-LN5B9U/hqdefault.jpg",
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
