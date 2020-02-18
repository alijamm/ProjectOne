package com.learn.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import com.learn.R
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.learn.adapters.RecyclerViewMainAdapter
import com.learn.models.Radio
import com.learn.service.VoiceService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private var flag: Boolean = false
    private val itemList: MutableList<Radio> = mutableListOf()
    private var brvahAdapter: RecyclerViewMainAdapter? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = 20 * 1000.toLong()
    private val FASTEST_INTERVAL: Long = 4000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(checkLocationPermission())
            startLocationUpdates()



        populateDummyData()
        brvahAdapter = RecyclerViewMainAdapter(R.layout.recycler_view_main_list, itemList)
        recycler_view_main.layoutManager = LinearLayoutManager(this)
        recycler_view_main.adapter = brvahAdapter
        brvahAdapter?.openLoadAnimation()
        brvahAdapter?.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->

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

    // Trigger new location updates at interval
    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = LocationRequest()
        mLocationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest?.setInterval(UPDATE_INTERVAL)
        mLocationRequest?.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        mLocationRequest?.also { builder.addLocationRequest(it) }
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // do work here
                    onLocationChanged(locationResult.getLastLocation())
                }
            },
            Looper.myLooper()
        )
    }

    fun onLocationChanged(location: Location?) {
        // New location has now been determined
//        val msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }


    fun getLastLocation() { // Get last known recent location using new Google Play Services SDK (v11+)
        val locationClient = getFusedLocationProviderClient(this)
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                // GPS location can be null if GPS is switched off
                location?.let { onLocationChanged(it) }
            }
            .addOnFailureListener { e ->
                Log.d("MapDemoActivity", "Error trying to get last GPS location")
                e.printStackTrace()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            99 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED
                ) { // permission was granted, yay! Do the
// location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) { //Request location updates:
                       startLocationUpdates()
                    }
                } else { // permission denied, boo! Disable the
// functionality that depends on this permission.
                }
                return
            }
        }

    }


    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) { // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) { // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                99
                            )
                        })
                    .create()
                    .show()
            } else { // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    99
                )
            }
            false
        } else {
            true
        }
    }

    private fun populateDummyData() {
        itemList.add(Radio("Mix FM Lebanon"))
        itemList.add(Radio("Mix FM Lebanon"))
        itemList.add(Radio("Al-Nour"))
        itemList.add(Radio("NRJ (Lebanon)"))
        itemList.add(Radio("Radio Lebanon"))
        itemList.add(Radio("Radio Maria"))
        itemList.add(Radio("Radio One (Lebanon)"))
        itemList.add(Radio("Mix FM Lebanon"))
        itemList.add(Radio("Radio Orient"))
        itemList.add(Radio("Voice of Lebanon"))
        itemList.add(Radio("Voice of the Mountain"))
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