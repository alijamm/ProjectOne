package com.learn.constants

import androidx.annotation.StringDef
import java.lang.annotation.RetentionPolicy


const val interval = 4000L // 1 Second


const val KEYWORD_TWO = "hello"

const val KEYWORD_THREE = "ECU"
const val KEYWORD_FOUR = "ecu"
const val KEYWORD_FIVE = "q"
const val KEYWORD_SIX = "queue"
const val KEYWORD_SEVEN = "a q"
const val KEYWORD_EIGHT = "hey"
const val KEYWORD_NINE = "hey q"


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