package com.learn.models

import com.learn.R
import com.learn.constants.RadioType

class Radio(
    var name: String,
    var type: RadioType,
    var logo: Int = R.drawable.ic_signal,
    var song: String? = null,
    var hasAds: Boolean = false,
    var adUrl: String? = null,
    var question: String? = null,
    var answers: ArrayList<String>? = null,
    var frequency: Double? = 0.0,
    var songArtist: String? = null,
    var lyrics: String = ""
) {

}