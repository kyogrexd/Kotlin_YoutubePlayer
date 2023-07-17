package com.example.kotlin_youtubeplayer.data

data class VideoDetailReq(val guestKey: String, val videoID: String, val mode: Int)

data class VideoDetailRes(val status: Int, val errMsgs: ArrayList<String>, val result: Result) {
    data class Result(val videoID: String, val userID: String, val userName: String, val userPhoto: String,
                      val content: String, val viewer: Int, val createTime: Long, val language: String,
                      val editorDetail: ArrayList<EditorDetail>, val audio: String, val privacy: Int,
                      val videoInfo: VideoInfo)

    data class EditorDetail(val language: String, val editorName: String)

    data class VideoInfo(val type: Int, val videourl: String, val title: String,
                         val thumbnails: String, val description: String, val publishedAt: String,
                         val duration: Long, val translatedLanguage: Int, val captionResult: CaptionResult)

    data class CaptionResult(val state: Int, val results: ArrayList<Results>, val collectionList: ArrayList<CollectionList>,
                             val collections: ArrayList<String>, val totalCorrectCount: Int, val totalRecordCount: Int,
                             val allLanguageCorrectCount: Int, val quizPronunceState: Float)

    data class Results(val language: String, val captions: ArrayList<Captions>)

    data class Captions(val time: Long, val content: String, val contentSimple: String,
                        val practiceCount: String, val correctCount: Int, val highestRate: Int,
                        val pronouncePractice: Int, val pronounceAccomplish: Int, val spellPractice:Int,
                        val spellAccomplish: Int, val recordCount: Int, val contentUserEdit: String,
                        val userTranslation: ArrayList<UserTranslation>, val pinyinList: ArrayList<PinyinList>)

    data class UserTranslation(val language: String, val content: String)

    data class PinyinList(val pinyinSplit: ArrayList<PinyinSplit>, val pinyinType: Int)

    data class PinyinSplit(val text: String, val pinyin: String)

    data class CollectionList(val vocabularyID: String, val vocabulary: String, val translated: String,
                              val practiceCount: Int, val correctCount: Int, val spellCorrectCount: Int)
}