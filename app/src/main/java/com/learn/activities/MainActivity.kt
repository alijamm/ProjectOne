package com.learn.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.learn.R
import com.learn.service.VoiceService
import com.vikramezhil.droidspeech.DroidSpeech
import com.vikramezhil.droidspeech.OnDSListener

class MainActivity : AppCompatActivity(), OnDSListener {


    private var droidSpeech: DroidSpeech? = null
    private var flag : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        droidSpeech = DroidSpeech(this, null)
        droidSpeech?.setOnDroidSpeechListener(this)

        button_start?.setOnClickListener {

            // startMyService()
            droidSpeech?.startDroidSpeechRecognition()
            flag = true
        }

        button_stop?.setOnClickListener {
            droidSpeech?.closeDroidSpeechOperations()
            flag = false

        }
    }


    override fun onResume() {
        super.onResume()
        stopMyService()
    }

    override fun onStop() {
        super.onStop()
        droidSpeech?.closeDroidSpeechOperations()
        if(flag)
            startMyService()
    }

    private fun startMyService() {
        // use this to start and trigger a service
        val i = Intent(this, VoiceService::class.java)
        // potentially add data to the intent
        i.putExtra("Key", "Hey Q")
        i.putExtra("Command","start service")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            this.startForegroundService(i)
        }else{
            this.startService(i)
        }
    }

    private fun stopMyService(){
        val i = Intent(this, VoiceService::class.java)
        this.stopService(i)
    }

    override fun onDroidSpeechSupportedLanguages(
        currentSpeechLanguage: String?,
        supportedSpeechLanguages: MutableList<String>?
    ) {
        if(supportedSpeechLanguages?.contains("en-EN") == true)
        {
            droidSpeech?.setPreferredLanguage("en-En")
        }
    }

    override fun onDroidSpeechError(errorMsg: String?) {
    }

    override fun onDroidSpeechClosedByUser() {
    }

    override fun onDroidSpeechLiveResult(liveSpeechResult: String?) {
    }

    override fun onDroidSpeechFinalResult(finalSpeechResult: String?) {

        Toast.makeText(this, "this : $finalSpeechResult", Toast.LENGTH_LONG).show()


    }

    override fun onDroidSpeechRmsChanged(rmsChangedValue: Float) {
    }

    override fun onDestroy() {
        super.onDestroy()
        droidSpeech?.closeDroidSpeechOperations()
    }
}