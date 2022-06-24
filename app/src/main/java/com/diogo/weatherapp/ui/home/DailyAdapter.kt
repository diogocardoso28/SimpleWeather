package com.diogo.weatherapp.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diogo.weatherapp.R
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

data class Daily(

    var dt: Int? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null,
    var moonrise: Int? = null,
    var moonset: Int? = null,
    var moonPhase: Double? = null,
    var temp: WeatherData.Temp? = WeatherData.Temp(),
    var feelsLike: WeatherData.FeelsLike? = WeatherData.FeelsLike(),
    var pressure: Int? = null,
    var humidity: Int? = null,
    var dewPoint: Double? = null,
    var windSpeed: Double? = null,
    var windDeg: Int? = null,
    var windGust: Double? = null,
    var weather: ArrayList<WeatherData.Weather> = arrayListOf(),
    var clouds: Int? = null,
    var pop: Double? = null,
    var uvi: Float? = null

)

class DailyAdapter(private val mDaily: ArrayList<WeatherData.Daily>?) :
    RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    var onItemClick: ((WeatherData.Daily) -> Unit)? = null
    var weatherData: ArrayList<WeatherData.Daily>? = mDaily;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val weekDayTextView = itemView.findViewById<TextView>(R.id.weekDay)
        val iconImageView = itemView.findViewById<ImageView>(R.id.dailyIcon)
        val minimumTemperature = itemView.findViewById<TextView>(R.id.minTemp)
        val maximumTemperature = itemView.findViewById<TextView>(R.id.maxTemp)
        //val rainPercentage = itemView.findViewById<TextView>(R.id.rainPercent)

        init {
            itemView.setOnClickListener {
                //TODO: This might be fucked
                val daily: WeatherData.Daily = mDaily?.get(adapterPosition) ?: WeatherData.Daily()
                onItemClick?.invoke(daily);
            }
        }
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val dailyView = inflater.inflate(R.layout.daily_weather_layout, parent, false)
        // Return a new holder instance
        return ViewHolder(dailyView)
    }

    // Involves populating data into the item through holder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: DailyAdapter.ViewHolder, position: Int) {
        // Get the data model based on position2
        val daily: WeatherData.Daily = mDaily?.get(position) ?: WeatherData.Daily()

        // Set item views based on your views and data model
        val weekDayTextView = viewHolder.weekDayTextView
        val imageView = viewHolder.iconImageView
        val minimumTemperatureTextView = viewHolder.minimumTemperature
        val maximumTemperatureTextView = viewHolder.maximumTemperature

        // val rainPercentageTextView = viewHolder.rainPercentage

        val longDt = daily.dt?.toLong()
        val dt = longDt?.let {
            Instant.ofEpochSecond(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
        if (dt != null) {

            weekDayTextView.text = dt.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        } else {
            weekDayTextView.setText("error")
        }

        minimumTemperatureTextView.text = daily.temp?.min?.roundToInt().toString() + "º"
        maximumTemperatureTextView.text = daily.temp?.max?.roundToInt().toString() + "º"

        //Load image to container
        val icon = daily.weather.get(0).icon.toString()

        val imageURL = "https://openweathermap.org/img/wn/$icon@2x.png"
        val mContext = viewHolder.iconImageView.context

        Picasso.with(mContext)
            .load(imageURL)
            .resize(200, 200)
            .into(imageView)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mDaily?.size ?: 0
    }


}