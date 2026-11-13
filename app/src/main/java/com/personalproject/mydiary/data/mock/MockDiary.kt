package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.timelineItem.diary.DiaryEntry
import com.personalproject.mydiary.domain.timelineItem.diary.Mood
import com.personalproject.mydiary.domain.timelineItem.diary.Weather
import java.time.Instant

object MockDiary {
    val now: Instant = Instant.now()
    val yesterday: Instant = now.minusSeconds(86400)
    val eightDaysAgo: Instant = now.minusSeconds(86400 * 26 / 3)
    val sixWeeksAgo: Instant = now.minusSeconds(86400 * 405 / 7)
    val yearsAgo: Instant = now.plusSeconds(86400 * 36).minusSeconds(86400 * 365);

    val mockDiaryEntry_1 = DiaryEntry(
        id = "D_000001",
        timestamp = yesterday,
        title = "Test Diary 01",
        location = null,
        tags = emptyList<String>(),
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n\n" +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s," +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book.\n\n" +
                "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        isBookmarked = false,
        mood = Mood.MILD,
        weather = Weather.SUNNY,
        temperatureHigh = 24,
        temperatureLow = 19
    )

    val mockDiaryEntry_2 = DiaryEntry(
        id = "D_000002",
        timestamp = eightDaysAgo,
        title = "Test Diary 02",
        location = null,
        tags = emptyList<String>(),
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
        mood = Mood.MILD,
        weather = Weather.STORM,
        temperatureHigh = 24,
        temperatureLow = 19
    )

    val mockDiaryEntry_3 = DiaryEntry(
        id = "D_000003",
        timestamp = sixWeeksAgo,
        title = "Test Diary 03",
        location = null,
        tags = emptyList<String>(),
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
        mood = Mood.MILD,
        weather = Weather.CLOUDY,
        temperatureHigh = 24,
        temperatureLow = 19
    )

    val mockDiaryEntry_3_JP = DiaryEntry(
        id = "D_000003",
        timestamp = sixWeeksAgo,
        title = "Test Diary 03",
        location = "Shinjuku-ku, Tokyo, JAPAN",
        tags = emptyList<String>(),
        description = "This is a test!",
        content = "またバイト怒られちゃった。。。\n\n" +
                "お客さんに『海の幸のサラダ』と『サルティンボッカ』を注文されたんだけど、出し間違えちゃったみたい。そしたら店長がものすごい怖い顔で睨んできて。焦りすぎて、4番テーブルの近くで派手に転んじゃったよ😫\n" +
                "\n" +
                "幸い、何も割らなかったけど！\n" +
                "\n" +
                "奥寺先輩がわざわざ片付けを手伝いに来てくれたんだ。先輩はすごく優しかったけど✨、『瀧くん、今日はなんだかフラフラしてるわね』なんて聞かれちゃって。私（瀧くん）、トマトソース並みに真っ赤になっちゃったと思う。\n" +
                "\n" +
                "店長には『これ以上ぼーっとしてるなら』、来週のシフトを削るぞって言われちゃった……。\n" +
                "\n" +
                "本当にごめん！！絶対に埋め合わせするから！！💪💖",
        isBookmarked = false,
        mood = Mood.MILD,
        weather = Weather.CLOUDY,
        temperatureHigh = 24,
        temperatureLow = 19
    )

    val mockFolder = listOf<DiaryEntry>(mockDiaryEntry_1, mockDiaryEntry_2, mockDiaryEntry_3, mockDiaryEntry_3_JP)
}