package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.core.util.DateUtil.Companion.daysAgo
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.Weather
import java.time.Instant

object MockDiary {
    val now: Instant = Instant.now()
    val yesterday: Instant = now.minusSeconds(86400)
    val eightDaysAgo: Instant = now.minusSeconds(86400 * 26 / 3)
    val sixWeeksAgo: Instant = now.minusSeconds(86400 * 405 / 7)
    // val oneYearAgo: Instant = now.plusSeconds(86400 * 36).minusSeconds(86400 * 365)

    val mockDiaryEntry_1 = DiaryEntry(
        id = "D_000001",
        folderId = "2",
        timestamp = yesterday,
        title = "Test Diary 01",
        colorHex = "#E57373",
        location = "Paris, France",
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n\n" +
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining " +
                "essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing " +
                "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including " +
                "versions of Lorem Ipsum.",
        isBookmarked = false,
        moodScore = 3,
        weather = Weather.SUNNY,
        temperatureHigh = 27,
        temperatureLow = 19
    )

    val mockDiaryEntry_2 = DiaryEntry(
        id = "D_000002",
        folderId = "1",
        timestamp = eightDaysAgo,
        title = "Test Diary 02",
        location = "Shinjuku-ku, Tokyo, JAPAN",
        description = "This is a test!",
        content = "HEY!! We need to talk.\n" +
                "\n" +
                "\"Cafe Hopping\"? What do you mean!?\n" +
                "\n" +
                "I agreed to the Kamakura trip. I agreed to the expensive sushi lunch for Takagi because I owed him. That was supposed to be it.\n" +
                "\n" +
                "I did not remember agreeing to you dragging my body around to a dozen dessert spots on a Saturday. I wanted to relax, maybe get the sushi over with and come home. Instead, you spent the whole day eating sweets and taking pictures? Huh??? That was my one day off this week! Not that I mind if you’re the one taking it, but like—\n" +
                "\n" +
                "You better have made Takagi split the bill for all those cafes. I know I said I'd treat him to the lunch, but if I find out you paid for his drinks and desserts too, I’m going to lose it. Don't let him rip you off just because you're feeling generous with my wallet.\n" +
                "\n" +
                "WORK: Because you blew so much cash on this, DO NOT MESS UP THE SHIFTS COMING UP!! I can't afford to lose this job or get our pay docked. If that happens, I'll make sure you pay for it.",
        isBookmarked = false,
        moodScore = 5,
        weather = Weather.STORM,
        temperatureHigh = 28,
        temperatureLow = 22
    )

    val mockDiaryEntry_3 = DiaryEntry(
        id = "D_000003",
        folderId = "2",
        timestamp = eightDaysAgo,
        title = "Test Diary 03",
        location = "Shinjuku-ku, Tokyo, JAPAN",
        colorHex = "#E57373",
        description = "This is a test!",
        content = "I messed up at work again...\n\n" +
                "Someone ordered the Insalata di Mare and the Saltimbocca, and I think I got them mixed up? And then the manager started glaring at me with those scary eyes. I got so nervous that I tripped over my own feet near Table 4\uD83D\uDE23\n" +
                "\n" +
                "I didn't break anything though!\n" +
                "\n" +
                "Okudera-senpai had to come over and help me clean up. She was so nice about it✨ but she asked if \"Taki-kun was feeling a little dizzy today.\" I think I (you?) turned as red as the tomato sauce.\n" +
                "\n" +
                "The manager said if I (you.) \"keep daydreaming,\" he's going to cut our hours next week...\n" +
                "\n" +
                "I'm so sorry!! I'll make it up to you!! \uD83D\uDCAA\uD83D\uDC96",
        isBookmarked = false,
        moodScore = 2,
        weather = Weather.CLOUDY,
        temperatureHigh = 24,
        temperatureLow = 19
    )

    val mockDiaryEntry_3_JP = DiaryEntry(
        id = "D_000004",
        folderId = "2",
        timestamp = daysAgo(now, 29),
        title = "東京生活９",
        location = "Shinjuku-ku, Tokyo, JAPAN",
        colorHex = "#E57373",
        description = "This is a test!",
        content = "またバイト怒られちゃった。。。\n\n" +
                "お客さんに『海の幸のサラダ』と『サルティンボッカ』" +
                "を注文されたんやけど、" +
                "出し間違えちゃったみたいやわ。" +
                "そしたら店長がものすごい怖い顔して睨んできてさ。" +
                "焦りすぎて、4番テーブルの近くで派手に転んじゃったんやて😫\n" +
                "\n" +
                "何も割らんかったけど\n" +
                "\n" +
                "奥寺先輩がわざわざ片付けを手伝いに来てくれたんだ。先輩はすごく優しか" +
                "ったんや✨" +
                "\n\n『瀧くん、今日はなんやフラフラしとるね』なんて聞かれちゃって。" +
                "瀧くん、トマトソース並みに真っ赤になっちゃったと思った\n" +
                "\n" +
                "店長には『これ以上ぼーっとしとるんなら！』、来週のシフトを減らすぞって言われちゃったんやて……\n" +
                "\n" +
                "ほんとごめんな！！絶対に埋め合わせするでな！！💪💖",
        isBookmarked = false,
        moodScore = 2,
        weather = Weather.CLOUDY,
        temperatureHigh = 21,
        temperatureLow = 16
    )

    val mockDiaryEntry_5 = DiaryEntry(
        id = "D_000005",
        folderId = "2",
        timestamp = daysAgo(now, 32),
        title = "東京生活８",
        location = "Shinjuku-ku, Tokyo, JAPAN",
        colorHex = "#E57373",
        description = "This is a test!",
        content = "小旅行❤\uFE0F鎌倉でカフェ巡り！\uD83C\uDFB6\n\n"
                + "東京のドリンクよりカッコイイ🍵✨\n"
                + "縁側から見えるお庭が最高すぎて、時間を忘れてまったりしちゃった🌿\n\n"
                + "そのあとは海沿いをブラブラお散歩して、江ノ電のレトロな雰囲気に癒やされた🚃🌊\n\n"
                + "自分の目で本物の海を見るのは今日が人生初…！\uD83E\uDD79\uD83C\uDF0A\n\n"
                + "リフレッシュ完了！💪💕\n\n",
        isBookmarked = false,
        moodScore = 4,
        weather = Weather.SUNNY,
        temperatureHigh = 24,
        temperatureLow = 21
    )

    val mockDiaryEntry_6 = DiaryEntry(
        id = "D_000006",
        folderId = "2",
        timestamp = daysAgo(now, 41),
        title = "Test Diary 02",
        colorHex = "#E57373",
        location = "Paris, France",
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n\n" +
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining " +
                "essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing " +
                "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including " +
                "versions of Lorem Ipsum.",
        isBookmarked = false,
        moodScore = 0,
        weather = Weather.SUNNY,
        temperatureHigh = 23,
        temperatureLow = 14
    )

    val mockDiaryEntry_7 = DiaryEntry(
        id = "D_000007",
        folderId = "2",
        timestamp = daysAgo(now, 52),
        title = "Test Diary 04",
        colorHex = "#E57373",
        location = "Paris, France",
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n\n" +
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining " +
                "essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing " +
                "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including " +
                "versions of Lorem Ipsum.",
        isBookmarked = false,
        moodScore = 0,
        weather = Weather.SUNNY,
        temperatureHigh = 27,
        temperatureLow = 19
    )

    val mockDiaryEntry_8 = DiaryEntry(
        id = "D_000008",
        folderId = "2",
        timestamp = daysAgo(now, 61),
        title = "Test Diary 01",
        colorHex = "#E57373",
        location = "Paris, France",
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n\n" +
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining " +
                "essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing " +
                "Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including " +
                "versions of Lorem Ipsum.",
        isBookmarked = false,
        moodScore = 3,
        weather = Weather.SUNNY,
        temperatureHigh = 27,
        temperatureLow = 19
    )
}
