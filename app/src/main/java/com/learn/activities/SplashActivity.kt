package com.learn.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.learn.R
import com.learn.constants.interval
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    private fun openHomePage() {


        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        startAnimation(1000)

        val handler = Handler()
        val runnable = Runnable {
                openHomePage()
        }
        handler.postDelayed(runnable, interval)
    }

    private fun startAnimation(rate: Long?) {


        val animation: Animation = AlphaAnimation(0f, 1f)

        rate?.also {
            animation.duration = it
        }
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        radioImage?.startAnimation(animation)
    }

}

