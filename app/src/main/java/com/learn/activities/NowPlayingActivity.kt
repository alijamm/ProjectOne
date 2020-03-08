package com.learn.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.learn.R
import com.learn.constants.RadioType
import com.learn.models.Radio
import kotlinx.android.synthetic.main.activity_now_playing.*


class NowPlayingActivity : AppCompatActivity() {

    private var radio: Radio? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)
        val gson = Gson()
        radio = gson.fromJson<Radio>(intent.getStringExtra("radio"), Radio::class.java)


        textView2?.text = "${radio?.name} \n ${radio?.song} \n ${radio?.songArtist} "
        nowPlayingContainer?.background = getDrawable(R.drawable.blueequalizervector)

    }


}