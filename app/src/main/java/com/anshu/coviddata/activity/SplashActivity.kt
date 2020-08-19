package com.anshu.coviddata.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.anshu.coviddata.R
import com.anshu.coviddata.util.ConnectionManager

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 1000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (ConnectionManager().checkConnectivity(this@SplashActivity)) {

            Handler().postDelayed({

                startActivity(Intent(this, TotalCases::class.java))
                finish()
            }, SPLASH_TIME_OUT)
        }
        else
        {
            val dialog = AlertDialog.Builder(this@SplashActivity)
            dialog.setTitle("error")
            dialog.setMessage("No internet")
            dialog.setPositiveButton("Open Setting"){
                    text, listener -> val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit"){
                    text, listener -> ActivityCompat.finishAffinity(this@SplashActivity)

            }
            dialog.create()
            dialog.show()
        }
    }

}
