package com.learn.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.learn.R
import com.learn.constants.RadioType
import com.learn.models.MessageEvent
import com.learn.models.Radio
import kotlinx.android.synthetic.main.activity_now_playing.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class NowPlayingActivity : AppCompatActivity(), View.OnClickListener {

    private var radio: Radio? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)
        val gson = Gson()
        radio = gson.fromJson<Radio>(intent.getStringExtra("radio"), Radio::class.java)
        camera?.setOnClickListener(this)
        like?.setOnClickListener(this)
        world?.setOnClickListener(this)
        callphone?.setOnClickListener(this)
        cart?.setOnClickListener(this)
        about?.setOnClickListener(this)
        maps?.setOnClickListener(this)



        when (radio?.type?.type) {

            RadioType.FILTER_MUSIC -> {
                imageView2?.setImageDrawable(getDrawable(R.drawable.ic_signal))
                talkPoll?.visibility=View.GONE
                adImage?.visibility = View.VISIBLE
                textView2?.visibility = View.VISIBLE
                textView2?.text = "${radio?.name} \n ${radio?.song} \n ${radio?.songArtist} "
                nowPlayingContainer?.background = getDrawable(R.drawable.blueequalizervector)
            }

            RadioType.FILTER_AD -> {
                imageView2?.visibility = View.GONE
                textView2?.text = "${radio?.name} \n ${radio?.song} \n ${radio?.songArtist} "
                nowPlayingContainer?.background = getDrawable(R.drawable.audiad)
                talkPoll?.visibility=View.GONE
                textView2?.visibility = View.VISIBLE
                adImage?.visibility = View.GONE
            }

            RadioType.FILTER_TALK -> {
                adImage?.visibility = View.GONE
                talkPoll?.visibility=View.VISIBLE
                imageView2?.background = getDrawable(R.drawable.radiodj)
                textView2?.visibility = View.GONE
                nowPlayingContainer?.background = getDrawable(R.drawable.giletsjaunes)
                adImage?.visibility = View.GONE

                question?.text = radio?.question
                answerone?.text = radio?.answers?.get(0)
                answertwo?.text = radio?.answers?.get(1)
                answerthree?.text = radio?.answers?.get(2)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when (event?.message) {
            "buy" -> {
                startPurchaseAction()
            }

            "call" -> {
                startPhoneAction()

            }

            "yes" -> {
                if (alertDialog != null) {
                    callphone?.setBackgroundColor(Color.YELLOW)
                    Toast.makeText(this, "Calling...", Toast.LENGTH_LONG).show()
                    val i = Intent(Intent.ACTION_DIAL, null)
                    alertDialog = null
                    startActivity(i)
                }
            }

            "location" -> {
                startMapAction()
            }

            "like"->{
                startLikeAction()
            }
            "about"->{
                showAboutDialog()
            }
        }
    }
    private fun startPurchaseAction(){
        cart?.setImageResource(R.drawable.ic_shopping_cart_bought)
        Toast.makeText(this, "item added to shopping cart", Toast.LENGTH_LONG).show()
    }
    private fun startMapAction() {
        maps?.setImageResource(R.drawable.ic_locationy)
        val uri: String =
            java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 48.864716f, 2.349014f)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        Toast.makeText(this, "Redirecting...", Toast.LENGTH_LONG).show()
        this.startActivity(intent)
    }
    private fun startPhoneAction(){
        var builder = AlertDialog.Builder(this);
        builder.setTitle("Contact Station");
        // set the custom layout
        builder.setMessage("Do you want to call this station ?")
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog?.dismiss()
            callphone?.setImageResource(R.drawable.ic_smartphoney)
            Toast.makeText(this, "Calling...", Toast.LENGTH_LONG).show()
            val toDial="tel:"+"14252053678";
            val i = Intent(Intent.ACTION_DIAL, Uri.parse(toDial))
            startActivity(i)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog?.dismiss()
            callphone?.setImageResource(R.drawable.ic_smartphone)
        }

        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog?.show();
        alertDialog?.setCancelable(false);
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }

    override fun onClick(v: View?) {
        when (v) {

            camera -> {
                requestCameraPermisison()
            }

            maps -> {
                startMapAction()
            }

            callphone -> {
                startPhoneAction()
            }

            world -> {
                world?.setImageResource(R.drawable.ic_worldwidey)
            }

            cart -> {
                startPurchaseAction()
            }

            about -> {
                showAboutDialog()
            }

            like -> {
                startLikeAction()
            }
        }
    }

    private fun startLikeAction(){
        Toast.makeText(this, "Added to favourites", Toast.LENGTH_SHORT).show()
        like?.setImageResource(R.drawable.ic_likey)
    }

    private fun showAboutDialog(){
        about?.setImageResource(R.drawable.ic_informationy)
        var builder = AlertDialog.Builder(this);
        builder.setTitle("Interactive Radio");
        // set the custom layout
        builder.setMessage("Features of this application include:\n" +
                "-Instant Polls\n" +
                "-Pictures of what is being discussed\n" +
                "-Details of interviewed personalities\n" +
                "-Video of what is being discussed\n" +
                "-Select question you want the DJ to ask the interviewee \n" +
                "-Live Streaming from the studio\n" +
                "-Chat using the phone app" +
                "-Personalized ad details" +
                "-Sponsors\n" +
                "-Listener engagement questions\n" +
                "-Real-time polls\n" +
                "-Chat using the phone app\n" +
                "-Artist Details\n" +
                "-Song Details")
        builder.setPositiveButton("Okay") { dialog, which ->
            dialog?.dismiss()
            about?.setImageResource(R.drawable.ic_information)
        }
        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog?.show();
        alertDialog?.setCancelable(false);
    }

    private fun requestCameraPermisison(){
        camera?.setImageResource(R.drawable.ic_cameray)

        ActivityCompat.requestPermissions(this,
             Array<String>(1){ Manifest.permission.CAMERA}, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Permission denied to open your camera ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}