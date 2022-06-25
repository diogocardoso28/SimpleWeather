package com.diogo.weatherapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.diogo.weatherapp.databinding.FragmentHomeBinding
import com.diogo.weatherapp.ui.home.WeatherData
import com.google.gson.Gson
import java.text.DateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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

        val longDt = daily.dt?.toLong() //Gets epoch time stamp
        //Parses it
        val dt = longDt?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

        //Put data on to XML
        if (dt != null) {
            dailyDateTextView.text = dt.format(DateTimeFormatter.ofPattern("d, MMM yyyy"))
        };
        minTemperatureTextView.text = daily.temp?.max.toString() + "º";
        maxTemperatureTextView.text = daily.temp?.min.toString() + "º";
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