package com.diogo.weatherapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.diogo.weatherapp.ui.home.WeatherData
import com.google.gson.Gson
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


class DetailedViewActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_view)

        //Put data on to xml and then show it
        val dailyString: String = intent.extras?.get(DAILY_DATA) as String;

        val daily: WeatherData.Daily = Gson().fromJson(dailyString, WeatherData.Daily::class.java);
        val minTemperatureTextView: TextView = findViewById<TextView>(R.id.minTemperature);
        val maxTemperatureTextView: TextView = findViewById<TextView>(R.id.maxTemperature);
        val dailyDateTextView: TextView = findViewById<TextView>(R.id.dailyDate);
        val iconImageView: ImageView = findViewById<ImageView>(R.id.iconImageView);

        val longDt = daily.dt?.toLong() //Gets epoch time stamp
        val icon = daily.weather.get(0).icon.toString()
        //Parses it
        val dt = longDt?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        //Put data on to XML
        if (dt != null) {
            dailyDateTextView.text = dt.format(DateTimeFormatter.ofPattern("EEEE ,d MMMM"))
        };
        minTemperatureTextView.text = daily.temp?.max?.roundToInt().toString() + "º";
        maxTemperatureTextView.text = daily.temp?.min?.roundToInt().toString() + "º";

        val context: Context = iconImageView.context

        Log.d("ICON", icon);

        val id = context.resources.getIdentifier("ic_" + icon, "drawable", context.packageName)
        iconImageView.setImageResource(id)

    }

    companion object {
        private val DAILY_DATA: String = "WEATHER";
        fun createIntent(context: Context?, weatherData: String?): Intent {
            return Intent(context, DetailedViewActivity::class.java).putExtra(
                DAILY_DATA,
                weatherData
            )
        }
    }

}