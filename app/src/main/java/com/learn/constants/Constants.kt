package com.learn.constants

import androidx.annotation.StringDef


const val interval = 2000L


const val KEYWORD_TWO = "hello"
const val KEYWORD_THREE = "ECU"
const val KEYWORD_FOUR = "ecu"
const val KEYWORD_FIVE = "q"
const val KEYWORD_SIX = "queue"
const val KEYWORD_SEVEN = "a q"
const val KEYWORD_EIGHT = "hey"
const val KEYWORD_NINE = "hey q"
const val RECEIVE_JSON = "com.your.package.RECEIVE_JSON"
const val NOTIFICATION_IDS = "q"

class RadioType(val type: String) {


    @StringDef(
        FILTER_AD,
        FILTER_MUSIC,
        FILTER_TALK
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class RadioType



    companion object {
        const val FILTER_AD = "ad"
        const val FILTER_MUSIC = "music"
        const val FILTER_TALK = "talk"
    }


}