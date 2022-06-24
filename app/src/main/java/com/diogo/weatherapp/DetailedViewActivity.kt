package com.diogo.weatherapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class DetailedViewActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_view)

        //TODO serialize this back into a daily object
        val dailyString:String = intent.extras?.get(DAILY_DATA) as String;


        Log.d("content",dailyString);
    }

    companion object {
        private val DAILY_DATA:String = "WEATHER";
        fun createIntent(context: Context?, weatherData: String?): Intent {
            return Intent(context, DetailedViewActivity::class.java).putExtra(DAILY_DATA, weatherData)
        }
    }

}