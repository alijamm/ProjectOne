package com.learn.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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


class NowPlayingActivity : AppCompatActivity() {

    private var radio: Radio? = null
    private var alertDialog: AlertDialog? = null

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
                imageView2?.background = getDrawable(R.drawable.radiodj)
                textView2?.visibility = View.GONE
                nowPlayingContainer?.background = getDrawable(R.drawable.giletsjaunes)
                adImage?.visibility = View.GONE
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when (event?.message) {
            "buy" -> {
                cart?.setBackgroundColor(Color.YELLOW)
                Toast.makeText(this,"item added to shopping cart",Toast.LENGTH_LONG).show()
            }

            "call" -> {
                var builder = AlertDialog.Builder(this);
                builder.setTitle("Turn on Radio");
                // set the custom layout
                builder.setMessage("Do you want to call this station ?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    dialog?.dismiss()
                    callphone?.setBackgroundColor(Color.YELLOW)
                    Toast.makeText(this,"Calling...",Toast.LENGTH_LONG).show()
                    val i = Intent(Intent.ACTION_DIAL, null)
                    startActivity(i)
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog?.dismiss()
                }

                // create and show the alert dialog
                alertDialog = builder.create();
                alertDialog?.show();
                alertDialog?.setCancelable(false);

            }

            "yes"->{
                if(alertDialog!=null){
                    callphone?.setBackgroundColor(Color.YELLOW)
                    Toast.makeText(this,"Calling...",Toast.LENGTH_LONG).show()
                    val i = Intent(Intent.ACTION_DIAL, null)
                    alertDialog = null
                    startActivity(i)
                }
            }

            "location"->{
                maps?.setBackgroundColor(Color.YELLOW)
                val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 48.864716f, 2.349014f)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                Toast.makeText(this,"Redirecting...",Toast.LENGTH_LONG).show()
                this.startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}