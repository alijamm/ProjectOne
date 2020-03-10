package com.learn.service

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.learn.BuildConfig
import com.learn.activities.MainActivity
import com.learn.constants.NOTIFICATION_IDS
import com.learn.models.MessageEvent
import com.vikramezhil.droidspeech.DroidSpeech
import com.vikramezhil.droidspeech.OnDSListener
import org.greenrobot.eventbus.EventBus


class VoiceService : Service(), OnDSListener {

    var context: Context = this
    var handler: Handler? = null
    var runnable: Runnable? = null

    private val NOTIFICATION_EX = 1
    private var droidSpeech: DroidSpeech? = null


    override fun onCreate() {
        super.onCreate()
        droidSpeech = DroidSpeech(this, null)
        droidSpeech?.setOnDroidSpeechListener(this)
        handler = Handler()
        runnable = Runnable {
            Log.d("Q", "service is running")
            handler?.postDelayed(runnable, 20000)
        }

        handler?.post(runnable)
        EventBus.getDefault().post( MessageEvent("alive"));
        droidSpeech?.startDroidSpeechRecognition()
    }

    override fun onBind(intent: Intent?): IBinder? {


        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent?.let { createPersistentNotificationO(it) }
        } else {
            intent?.let { createPersistentNotificationPreO(it) }
        }
        return START_STICKY
    }
    override fun onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler?.removeCallbacks(runnable)
        droidSpeech?.closeDroidSpeechOperations()
    }

    override fun onStart(intent: Intent, startid: Int) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show()
    }

    private fun createPersistentNotificationPreO(intent: Intent) {
        val contentTitle = "My Service"
        val contentText = "You have a running service !"

        // Create Pending Intents.
//        val piLaunchMainActivity = getLaunchActivityPI(context)
//        val piStopService = getStopServicePI(context)

        // Action to stop the service.
        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.stat_notify_sync,
            contentTitle,
            PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        ).build()

        // Create a notification.
        val mNotification = NotificationCompat.Builder(context)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setContentIntent(
                PendingIntent.getService(
                    context,
                    2,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(stopAction)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        this.startForeground(
            NOTIFICATION_EX, mNotification
        )
    }

    private fun createPersistentNotificationO(intent: Intent) {
        val contentTitle = "My Service"

        val contentText = "You have a running service !"
        // Action to stop the service.
        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.stat_notify_sync,
            contentTitle,
            PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        ).build()
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

    override fun onDroidSpeechSupportedLanguages(
        currentSpeechLanguage: String?,
        supportedSpeechLanguages: MutableList<String>?
    ) {
        if (supportedSpeechLanguages?.contains("en-US") == true) {
            droidSpeech?.setPreferredLanguage("en-US")
        }
    }

    override fun onDroidSpeechError(errorMsg: String?) {
        Log.d("Q", "error speech $errorMsg")

    }

    override fun onDroidSpeechClosedByUser() {

        Log.d("Q", "closed by user speech")

    }

    override fun onDroidSpeechLiveResult(liveSpeechResult: String?) {

        Log.d("Q", "live speech $liveSpeechResult")
    }

    override fun onDroidSpeechFinalResult(finalSpeechResult: String?) {
        finalSpeechResult?.toLowerCase()
        if (finalSpeechResult?.indexOf("hey") != -1 || finalSpeechResult?.indexOf("hello") != -1|| finalSpeechResult?.indexOf("ecu") != -1) {
            if (finalSpeechResult?.indexOf("q") != -1 || finalSpeechResult?.indexOf("queue") != -1 || finalSpeechResult?.indexOf("cute") != -1||finalSpeechResult?.indexOf("ecu") != -1) {
                val i = Intent()
                i.setClass(this, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                return
            }
        }

        if (finalSpeechResult?.indexOf("location") != -1 || finalSpeechResult?.indexOf("nearest") != -1) {
            EventBus.getDefault().post( MessageEvent("location"))
            return
        }

        if (finalSpeechResult?.indexOf("what") != -1) {
            if (finalSpeechResult?.indexOf("playing") != -1 || finalSpeechResult?.indexOf("radio") != -1) {
                EventBus.getDefault().post( MessageEvent("play"));
                return
            }
        }

        if (finalSpeechResult?.indexOf("buy") != -1 || finalSpeechResult?.indexOf("purchase") != -1) {
            EventBus.getDefault().post( MessageEvent("buy"));
            return
        }

        if (finalSpeechResult?.indexOf("call") != -1 || finalSpeechResult?.indexOf("number") != -1) {
            EventBus.getDefault().post( MessageEvent("call"));
            return
        }

        if (finalSpeechResult?.indexOf("like") != -1 || finalSpeechResult?.indexOf("favourite") != -1) {
            EventBus.getDefault().post( MessageEvent("like"));
            return
        }

        if (finalSpeechResult?.indexOf("about") != -1 || finalSpeechResult?.indexOf("help") != -1) {
            EventBus.getDefault().post( MessageEvent("about"));
            return
        }

        if(finalSpeechResult?.indexOf("yes")!=-1){
            EventBus.getDefault().post( MessageEvent("yes"));
            return
        }

    }

    override fun onDroidSpeechRmsChanged(rmsChangedValue: Float) {
    }
}