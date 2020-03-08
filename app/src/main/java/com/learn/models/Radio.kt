package com.learn.models

import com.learn.constants.RadioType

class Radio(var name: String, var type: RadioType,

     var song: String? = null,
     var hasAds : Boolean = false,
     var adUrl : String? = null,
     var question : String? = null,
     var answers : String? = null,
    var songArtist:String? = null){

}