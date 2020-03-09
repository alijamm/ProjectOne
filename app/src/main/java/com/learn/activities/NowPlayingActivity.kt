package com.learn.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
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

        when (radio?.type?.type) {

            RadioType.FILTER_MUSIC -> {
                textView2?.text = "${radio?.name} \n ${radio?.song} \n ${radio?.songArtist} "
                nowPlayingContainer?.background = getDrawable(R.drawable.blueequalizervector)
            }

            RadioType.FILTER_AD -> {
                imageView2?.visibility = View.GONE
                textView2?.text = "${radio?.name} \n ${radio?.song} \n ${radio?.songArtist} "
                nowPlayingContainer?.background = getDrawable(R.drawable.audiad)
                adImage?.visibility = View.GONE
            }

            RadioType.FILTER_TALK -> {
                imageView2?.background=getDrawable(R.drawable.radiodj)
                textView2?.visibility = View.GONE
                nowPlayingContainer?.background = getDrawable(R.drawable.giletsjaunes)
                adImage?.visibility = View.GONE
            }
        }
    }


}