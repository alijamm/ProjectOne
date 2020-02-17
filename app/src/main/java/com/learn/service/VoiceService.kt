package com.learn.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import android.os.Handler
import android.app.NotificationManager
import java.util.*
import android.app.PendingIntent
import com.learn.activities.MainActivity
import androidx.core.app.NotificationCompat
import android.os.Build
import com.learn.constants.KEYWORD_TWO
import android.app.NotificationChannel
import com.vikramezhil.droidspeech.DroidSpeech
import com.vikramezhil.droidspeech.OnDSListener


class VoiceService : Service(), OnDSListener {

    override fun onDroidSpeechSupportedLanguages(
        currentSpeechLanguage: String?,
        supportedSpeechLanguages: MutableList<String>?
    ) {
        if(supportedSpeechLanguages?.contains("en-CA") == true)
        {
            droidSpeech?.setPreferredLanguage("en-CA")
        }
    }

    override fun onDroidSpeechError(errorMsg: String?) {

    }

    override fun onDroidSpeechClosedByUser() {
    }

    override fun onDroidSpeechLiveResult(liveSpeechResult: String?) {
    }

    override fun onDroidSpeechFinalResult(finalSpeechResult: String?) {



        if(finalSpeechResult?.equals(KEYWORD_TWO) == true){

            val i = Intent()
            i.setClass(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)

        }else{

            Toast.makeText(this, "output : $finalSpeechResult", Toast.LENGTH_LONG).show()
        }





    }

    override fun onDroidSpeechRmsChanged(rmsChangedValue: Float) {
    }

    var context: Context = this
    var handler: Handler? = null
    var runnable: Runnable? = null


    private val UPDATE_INTERVAL = 60 * 1000
    private val timer = Timer()
    private val NOTIFICATION_EX = 1
    private var notificationManager: NotificationManager? = null
    private var droidSpeech: DroidSpeech? = null


    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show()

        handler = Handler()
        runnable = Runnable {
            Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show()
            handler?.postDelayed(runnable, 10000)
        }

        handler?.post(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            intent?.let { createPersistentNotificationO(it) }
        }else{
            intent?.let { createPersistentNotificationPreO(it) }
        }
        droidSpeech = DroidSpeech(this, null)
        droidSpeech?.setOnDroidSpeechListener(this)
        droidSpeech?.startDroidSpeechRecognition()

        return START_STICKY
    }


    private fun createPersistentNotificationPreO(intent: Intent){
        val contentTitle = "My Service"
        val contentText = "You have a running service !"

        // Create Pending Intents.
//        val piLaunchMainActivity = getLaunchActivityPI(context)
//        val piStopService = getStopServicePI(context)

        // Action to stop the service.
        val stopAction = NotificationCompat.Action.Builder(android.R.drawable.stat_notify_sync,contentTitle, PendingIntent.getService(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)).build()

        // Create a notification.
        val mNotification = NotificationCompat.Builder(context)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setContentIntent(PendingIntent.getService(context,2,intent,PendingIntent.FLAG_UPDATE_CURRENT))
            .addAction(stopAction)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        this.startForeground(
            NOTIFICATION_EX, mNotification
        )
    }

    private fun createPersistentNotificationO(intent: Intent){
        val contentTitle = "My Service"

        val contentText = "You have a running service !"
        // Action to stop the service.
        val stopAction = NotificationCompat.Action.Builder(android.R.drawable.stat_notify_sync,contentTitle, PendingIntent.getService(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)).build()
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, "foreroundServiceChannel")
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setContentIntent(pendingIntent)
            .addAction(stopAction)
            .build()

        startForeground(1, notification)

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "foreroundServiceChannel",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }


    override fun onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler?.removeCallbacks(runnable)
        droidSpeech?.closeDroidSpeechOperations()
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show()
    }

    override fun onStart(intent: Intent, startid: Int) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show()
        droidSpeech?.startDroidSpeechRecognition()
    }
}