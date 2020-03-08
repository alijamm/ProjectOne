package com.learn.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import com.learn.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.gson.Gson
import com.learn.adapters.RecyclerViewMainAdapter
import com.learn.constants.RadioType
import com.learn.models.Radio
import com.learn.service.VoiceService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private var flag: Boolean = false
    private val itemList: MutableList<Radio> = mutableListOf()
    private var brvahAdapter: RecyclerViewMainAdapter? = null
    private val LOADING_TIME : Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showProgressBarFor(LOADING_TIME)
        brvahAdapter = RecyclerViewMainAdapter(R.layout.recycler_view_main_list, itemList)
        recycler_view_main.layoutManager = GridLayoutManager(this,2)
        recycler_view_main.adapter = brvahAdapter
        brvahAdapter?.openLoadAnimation()
        brvahAdapter?.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener() { baseQuickAdapter: BaseQuickAdapter<Any, BaseViewHolder>, view: View, i: Int ->

                Log.d("RADIO","on item click ")

                openNowPlayingRadioActivity(baseQuickAdapter.data[i] as Radio)
            }

        val prefs = getSharedPreferences("switch", Context.MODE_PRIVATE)
        flag = prefs.getBoolean("name", false)

        switch_button?.isChecked = flag

        switch_button?.setOnCheckedChangeListener { button, isChecked ->
            val editor = prefs.edit()
            editor.putBoolean("name", isChecked)
            editor.apply()

            if (isChecked)
                startMyService()
            else
                stopMyService()
        }
    }

    private fun openNowPlayingRadioActivity(radio: Radio) {
        val intent = Intent()
        intent.setClass(this, NowPlayingActivity::class.java)
        val gson = Gson()
        intent.putExtra("radio", gson.toJson(radio))
        startActivity(intent)
    }


    private fun showProgressBarFor(interval : Long){

        Handler().postDelayed(Runnable {
            progress_bar?.visibility = View.GONE
            loadingMessage?.visibility = View.GONE
        populateDummyData()
        },interval)
    }


    private fun populateDummyData() {
        itemList.add(Radio("Mix FM Lebanon",RadioType(RadioType.FILTER_MUSIC),song = "California Dreamin",songArtist = "SIA"))
        itemList.add(Radio("Mix FM Lebanon",RadioType(RadioType.FILTER_MUSIC)))
        itemList.add(Radio("Al-Nour", RadioType(RadioType.FILTER_AD)))
        itemList.add(Radio("NRJ (Lebanon)",RadioType(RadioType.FILTER_AD)))
        itemList.add(Radio("Radio Lebanon",RadioType(RadioType.FILTER_AD)))
        itemList.add(Radio("Radio Maria",RadioType(RadioType.FILTER_TALK)))
        itemList.add(Radio("Radio One (Lebanon)",RadioType(RadioType.FILTER_TALK)))
        itemList.add(Radio("Mix FM Lebanon",RadioType(RadioType.FILTER_TALK)))
        itemList.add(Radio("Radio Orient",RadioType(RadioType.FILTER_MUSIC)))
        itemList.add(Radio("Voice of Lebanon",RadioType(RadioType.FILTER_AD)))
        itemList.add(Radio("Voice of the Mountain",RadioType("fifiefe")))
        brvahAdapter?.notifyDataSetChanged()
        recycler_view_main?.visibility=View.VISIBLE
    }

    private fun startMyService() {
        // use this to start and trigger a service
        val i = Intent(this, VoiceService::class.java)
        // potentially add data to the intent
        i.putExtra("Key", "Hey Q")
        i.putExtra("Command", "start service")
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