package com.learn.activities

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.learn.R
import com.learn.adapters.RecyclerViewMainAdapter
import com.learn.constants.RadioType
import com.learn.models.MessageEvent
import com.learn.models.Radio
import com.learn.service.VoiceService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view_main_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    private var flag: Boolean = false
    private val itemList: MutableList<Radio> = mutableListOf()
    private var brvahAdapter: RecyclerViewMainAdapter? = null
    private val LOADING_TIME: Long = 3000
    private var speaker: TextToSpeech? = null
    private var alertDialog: AlertDialog? = null
    private var local: LocalBroadcastManager? = null
    private var openNowPlayingConfirmed : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        local = LocalBroadcastManager.getInstance(this)
        showProgressBarFor(LOADING_TIME)
        brvahAdapter = RecyclerViewMainAdapter(R.layout.recycler_view_main_list, itemList)
        recycler_view_main?.layoutManager = LinearLayoutManager(this)
        recycler_view_main?.adapter = brvahAdapter
        brvahAdapter?.openLoadAnimation()
        brvahAdapter?.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener() { baseQuickAdapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, i: Int ->
                openNowPlayingConfirmed = true


                var builder = AlertDialog.Builder(this);
                builder.setTitle("Radio");
                // set the custom layout
                val inflater = this.layoutInflater
                builder.setView(inflater.inflate(R.layout.listen_dialog, null))
//                builder.setMessage("Listen to  ${(baseQuickAdapter.data[i] as Radio).name} ?")
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog?.dismiss()
                    openNowPlayingConfirmed = false
                }

                // create and show the alert dialog
                alertDialog = builder.create();
                alertDialog?.show();
                alertDialog?.setCancelable(false);
                Handler().postDelayed(Runnable {
                    alertDialog?.dismiss()
                    if(openNowPlayingConfirmed)
                        openNowPlayingRadioActivity(baseQuickAdapter.data[i] as Radio)
                }, 2347L)

            }

        val prefs = getSharedPreferences("switch", Context.MODE_PRIVATE)
        flag = prefs.getBoolean("name", false)
        speaker = TextToSpeech(this,
            OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    speaker?.language = Locale.US
                }
            })

        switch_button?.isChecked = flag

        switch_button?.setOnCheckedChangeListener { button, isChecked ->
            val editor = prefs.edit()
            editor.putBoolean("name", isChecked)
            editor.apply()

            if (isChecked) {
                textToSpeech("q ready")
                startMyService()

            } else
                stopMyService()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
//        Toast.makeText(this,"${event?.message}",Toast.LENGTH_LONG).show()

        when(event?.message){
            "play"->{
                openNowPlayingConfirmed = true
                var builder = AlertDialog.Builder(this);
                builder.setTitle("Radio");
                // set the custom layout
                val inflater = this.layoutInflater
                builder.setView(inflater.inflate(R.layout.listen_dialog, null))
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog?.dismiss()
                    openNowPlayingConfirmed = false
                }

                // create and show the alert dialog
                alertDialog = builder.create();
                alertDialog?.show();
                alertDialog?.setCancelable(false);
                Handler().postDelayed(Runnable {
                    alertDialog?.dismiss()
                    if(openNowPlayingConfirmed)
                        openNowPlayingRadioActivity(itemList[(Math.random() * ( 5 )).toInt()])
                }, 2347L)
                textToSpeech("now playing on radio")
            }
        }
    }
    private fun textToSpeech(s : String){
        if (Build.VERSION.SDK_INT >= 21) {
            speaker?.speak(s, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            speaker?.speak(s, TextToSpeech.QUEUE_ADD, null)
        }
    }


    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                 Array<String>(9){Manifest.permission.RECORD_AUDIO},
                9);
        }

        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (speaker != null) {
            speaker?.stop()
            speaker?.shutdown()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun openNowPlayingRadioActivity(radio: Radio) {
        val intent = Intent()
        intent.setClass(this, NowPlayingActivity::class.java)
        val gson = Gson()
        intent.putExtra("radio", gson.toJson(radio))
        startActivity(intent)
    }


    private fun showProgressBarFor(interval: Long) {

        Handler().postDelayed(Runnable {
            progress_bar?.visibility = View.GONE
            loadingMessage?.visibility = View.GONE
            populateDummyData()
        }, interval)
    }


    private fun populateDummyData() {
        val answers : ArrayList<String>? = arrayListOf()
        answers?.add("• yes")
        answers?.add("• no")
        answers?.add("• completely confused")
        itemList.add(
            Radio(
                "680 The Heat",
                RadioType(RadioType.FILTER_MUSIC),
                song = "California Dreamin",
                songArtist = "SIA", frequency = 99.4,logo =  R.drawable.theheat
            )
        )
        itemList.add(Radio("WWRC The Answer\n1260 AM", RadioType(RadioType.FILTER_MUSIC),song = "Malibu",
            songArtist = "Miley Cyrus",logo = R.drawable.wwrc, frequency = 99.3))
        itemList.add(Radio("WAVA 105.1", RadioType(RadioType.FILTER_AD),R.drawable.wava,songArtist = "Audi",song = "A7 Sedan", frequency = 109.3))
        itemList.add(Radio("Voice of America", RadioType(RadioType.FILTER_AD), songArtist = "Audi",song = "A7 Sedan",logo = R.drawable.voa,frequency = 89.3))
        itemList.add(Radio("WETA", RadioType(RadioType.FILTER_AD), songArtist = "Audi",song = "A7 Sedan",logo = R.drawable.weta,frequency = 92.4))
        itemList.add(Radio("Beats 360", RadioType(RadioType.FILTER_TALK),logo = R.drawable.beats, question = "What do you think of whats happening ?\nDo you support the yellow vests?",
            answers = answers,frequency = 90.1))
        itemList.add(
            Radio(
                "WHBC",
                RadioType(RadioType.FILTER_TALK),logo = R.drawable.whbc,
                frequency = 78.3,question = "What do you think of whats happening ?\nDo you support the yellow vests?",
                answers = answers
            )
        )
        itemList.add(Radio("WFED Federal News Radio", RadioType(RadioType.FILTER_TALK),logo = R.drawable.fedr,question = "What do you think of whats happening ?\nDo you support the yellow vests?",
            answers = answers, frequency = 99.0))
        itemList.add(Radio("Retro 80's", RadioType(RadioType.FILTER_MUSIC),logo = R.drawable.retro, song = "Monsters",songArtist = "Rihanna",frequency = 100.0))
        itemList.add(Radio("WPFW", RadioType(RadioType.FILTER_AD),songArtist = "Audi",logo = R.drawable.wpfw,song = "A7 Sedan", frequency = 89.9))
        brvahAdapter?.notifyDataSetChanged()
        recycler_view_main?.visibility = View.VISIBLE
    }

    private fun startMyService() {
        // use this to start and trigger a service
        val i = Intent(this, VoiceService::class.java)
        // potentially add data to the intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(i)
        } else {
            this.startService(i)
        }
    }

    private fun stopMyService() {
        val i = Intent(this, VoiceService::class.java)
        this.stopService(i)
    }

}