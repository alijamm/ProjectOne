package com.learn.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MainActivity : AppCompatActivity() {


    private var flag: Boolean = false
    private val itemList: MutableList<Radio> = mutableListOf()
    private var brvahAdapter: RecyclerViewMainAdapter? = null
    private val LOADING_TIME: Long = 3000
    private var speaker: TextToSpeech? = null
    private var alertDialog: AlertDialog? = null
    private var local: LocalBroadcastManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        local = LocalBroadcastManager.getInstance(this)
        showProgressBarFor(LOADING_TIME)
        brvahAdapter = RecyclerViewMainAdapter(R.layout.recycler_view_main_list, itemList)
        recycler_view_main?.layoutManager = GridLayoutManager(this, 2)
        recycler_view_main?.adapter = brvahAdapter
        brvahAdapter?.openLoadAnimation()
        brvahAdapter?.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener() { baseQuickAdapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, i: Int ->
                var builder = AlertDialog.Builder(this);
                builder.setTitle("Turn on Radio");
                // set the custom layout
                builder.setMessage("Navigate to ${(baseQuickAdapter.data[i] as Radio).frequency}MHz for radio interaction with ${(baseQuickAdapter.data[i] as Radio).name}")
                builder.setPositiveButton("Listen") { dialog, which ->

                    dialog?.dismiss()
                    openNowPlayingRadioActivity(baseQuickAdapter.data[i] as Radio)
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog?.dismiss()
                }

                // create and show the alert dialog
                alertDialog = builder.create();
                alertDialog?.show();
                alertDialog?.setCancelable(false);
                Log.d("RADIO", "on item click ")


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
        Toast.makeText(this,"${event?.message}",Toast.LENGTH_LONG).show()

        when(event?.message){
            "play"->{
                openNowPlayingRadioActivity(itemList[(Math.random() * ( 5 )).toInt()])
                textToSpeech("now playing on radio")
            }
            "buy"->{
                textToSpeech("q is buying this item for you")
            }
            "call"->{
                textToSpeech("q will call this number for you")
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
        itemList.add(
            Radio(
                "Mix FM Lebanon",
                RadioType(RadioType.FILTER_MUSIC),
                song = "California Dreamin",
                songArtist = "SIA"
            )
        )
        itemList.add(Radio("Mix FM Lebanon", RadioType(RadioType.FILTER_MUSIC), frequency = 99.3))
        itemList.add(Radio("Al-Nour", RadioType(RadioType.FILTER_AD), frequency = 109.3))
        itemList.add(Radio("NRJ (Lebanon)", RadioType(RadioType.FILTER_AD), frequency = 89.3))
        itemList.add(Radio("Radio Lebanon", RadioType(RadioType.FILTER_AD), frequency = 92.4))
        itemList.add(Radio("Radio Maria", RadioType(RadioType.FILTER_TALK), frequency = 90.1))
        itemList.add(
            Radio(
                "Radio One (Lebanon)",
                RadioType(RadioType.FILTER_TALK),
                frequency = 78.3
            )
        )
        itemList.add(Radio("Mix FM Lebanon", RadioType(RadioType.FILTER_TALK), frequency = 99.0))
        itemList.add(Radio("Radio Orient", RadioType(RadioType.FILTER_MUSIC), frequency = 100.0))
        itemList.add(Radio("Voice of Lebanon", RadioType(RadioType.FILTER_AD), frequency = 89.9))
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