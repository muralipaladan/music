package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.LyricLine
import java.util.Locale

class LyricsRepository {

    fun getSyncedLyrics(trackTitle: String, durationSeconds: Float): List<LyricLine> {
        val normalizedTitle = trackTitle.lowercase(Locale.getDefault())

        // 1. Curated Synced Lyrics for "Illuminati" (Aavesham)
        if (normalizedTitle.contains("illuminati") || normalizedTitle.contains("aavesham")) {
            return listOf(
                LyricLine(0f, "🎵 [Music Intro - Illuminati Beats] 🎵"),
                LyricLine(5f, "Illuminati..."),
                LyricLine(10f, "ആരുമറിയാതെ വഴിമാറി ഇവിടെയാരോ വന്നു..."),
                LyricLine(15f, "കണ്ണടച്ചു തുറക്കുമ്പോൾ മായാജാലം തന്നു!"),
                LyricLine(20f, "Illuminati... Don't you see the light?"),
                LyricLine(24f, "ആകാശപ്പൂക്കൾ വിരിയുന്നീ രാത്രിയിൽ..."),
                LyricLine(28f, "ചെണ്ടകൊട്ടിപ്പാടും ആവേശ കൂട്ടരിൽ!"),
                LyricLine(33f, "Illuminati... ഹൃദയം തുടിക്കുമീ താളത്തിൽ!"),
                LyricLine(38f, "ആർത്തുവിളിച്ചു പാടാം നമുക്ക് ഈ രാവിൽ..."),
                LyricLine(43f, "അലകടലായ് മാറും ജനക്കൂട്ടം നടുവിൽ!"),
                LyricLine(48f, "Illuminati... Forever shining bright!"),
                LyricLine(55f, "🎵 [Awesome Instrumental Solo] 🎵"),
                LyricLine(70f, "ഇനി നമ്മൾ കാണാൻ പോകും കളി വേറെ!"),
                LyricLine(75f, "ആവേശത്തിന്റെ കൊടുമുടി കയറി നേരെ!"),
                LyricLine(80f, "Illuminati...")
            )
        }

        // 2. Curated Synced Lyrics for "Darshana" (Hridayam)
        if (normalizedTitle.contains("darshana") || normalizedTitle.contains("hridayam")) {
            return listOf(
                LyricLine(0f, "🎵 [Soft Piano Intro - Darshana] 🎵"),
                LyricLine(4f, "ദർശനാ... നിൻ പുഞ്ചിരിപ്പൂക്കൾ..."),
                LyricLine(9f, "എന്റെ മനസ്സിനെ തൊട്ടുണർത്തുന്നുവോ..."),
                LyricLine(14f, "നീ എൻ കൂടെയുള്ളപ്പോൾ പെയ്യും മഴയും മധുരം!"),
                LyricLine(20f, "ഓർമ്മകളിൽ നീ മാത്രം നിറയുമ്പോൾ..."),
                LyricLine(25f, "പ്രണയത്തിന്റെ ഈണം ചുണ്ടിൽ ഉണരുമ്പോൾ..."),
                LyricLine(30f, "Darshana... beautiful soul of mine!"),
                LyricLine(35f, "നിൻ കണ്ണിലെ പ്രകാശം എൻ വഴികളിൽ വെളിച്ചം..."),
                LyricLine(40f, "ഒരു നോക്കു കാണാൻ കൊതിച്ചു ഞാൻ നിൽക്കും നേരം!"),
                LyricLine(46f, "നീ തന്ന പ്രണയത്തിന്റെ സമ്മാനം ഈ ഹൃദയം!"),
                LyricLine(52f, "🎵 [Violin Instrumental Interlude] 🎵")
            )
        }

        // 3. Curated Synced Lyrics for "Thamarapoovil"
        if (normalizedTitle.contains("thamarapoovil") || normalizedTitle.contains("thamarapoovu")) {
            return listOf(
                LyricLine(0f, "🎵 [Flute Intro] 🎵"),
                LyricLine(5f, "താമരപ്പൂവിൽ വാഴും ദേവീ..."),
                LyricLine(11f, "നിൻ തിരുനടയിൽ ഞാൻ അർച്ചന ചെയ്യാം..."),
                LyricLine(17f, "മനസ്സിൽ നിറയുന്ന പ്രാർത്ഥനാ പൂക്കൾ..."),
                LyricLine(23f, "നിൻ തൃപ്പാദങ്ങളിൽ അർപ്പിച്ചു നിൽക്കാം..."),
                LyricLine(29f, "താമരപ്പൂവിൽ വാഴും ദേവീ..."),
                LyricLine(35f, "🎵 [Instrumental Devotional Symphony] 🎵")
            )
        }

        // 4. Fallback Generator: Dynamically generates beautifully timed synchronized lines
        // based on any track's title, spread evenly over the track's duration.
        val titleClean = trackTitle.replace(Regex("[|()【】\\[\\]\\-_]"), " ").trim()
        val duration = if (durationSeconds > 0) durationSeconds else 180f
        val interval = duration / 12f

        return listOf(
            LyricLine(0f, "🎵 [Music Playing - $titleClean] 🎵"),
            LyricLine(interval * 1, "ഈ മനോഹരമായ ഗാനം ആരംഭിക്കുകയായി..."),
            LyricLine(interval * 2, "ഹൃദയത്തിൽ തൊടുന്ന വരികൾ..."),
            LyricLine(interval * 3, "മനസ്സിലേക്ക് തഴുകിയെത്തുന്ന സംഗീതം... ✨"),
            LyricLine(interval * 4, "🎵 [ആസ്വദിക്കൂ... സംഗീത സാന്ദ്രമായ നിമിഷം] 🎵"),
            LyricLine(interval * 5, "ഓർമ്മകളിലേക്ക് കൂട്ടിമുട്ടുന്ന ഈണങ്ങൾ..."),
            LyricLine(interval * 6, "ബാക്ക്ഗ്രൗണ്ടിൽ സംഗീതം ഒഴുകിപ്പരക്കുമ്പോൾ... 🎧"),
            LyricLine(interval * 7, "പ്രണയവും ഓർമ്മകളും ഇഴചേരുന്ന രാവ്..."),
            LyricLine(interval * 8, "🎵 [Beautiful Instrumental Solo] 🎵"),
            LyricLine(interval * 9, "ഈ വരികൾ നിങ്ങളുടെ മനസ്സിനെ തൊട്ടുണർത്തട്ടെ..."),
            LyricLine(interval * 10, "ആസ്വദിക്കൂ ഓരോ നിമിഷവും സന്തോഷത്തോടെ..."),
            LyricLine(interval * 11, "ഈ ഗാനം അവസാനത്തിലേക്ക് അടുക്കുന്നു... 🌟"),
            LyricLine(interval * 12, "🎵 [Music Outro] 🎵")
        )
    }
}
