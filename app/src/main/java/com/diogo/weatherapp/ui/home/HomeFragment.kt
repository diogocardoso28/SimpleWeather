package com.diogo.weatherapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diogo.weatherapp.R
import com.diogo.weatherapp.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@DelicateCoroutinesApi
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Updates ui after page loads
        updateUi()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getName(names: WeatherData.LocalNames, languageId: String): String {
        //Todo: better handle local names
        return if (languageId == "pt" || names.en == null) {
            names.pt.toString()
        } else {
            names.en.toString()
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    private suspend fun getWeatherData(locaton: WeatherData.LocationData): WeatherData.WeatherData {
        var result = WeatherData.WeatherData()
        val filename = "weather"
        var cacheFile = File(context?.cacheDir, filename)
        var pullFromApi = true
        if (cacheFile.exists()) {
            val lastModified = Date(cacheFile.lastModified()).time
            val currTime = (java.util.Calendar.getInstance()).time.time
            val diff: Long = currTime - lastModified
            val seconds = diff / 1000
            val minutes = seconds / 60
            Log.d("TimeDiff", minutes.toString())
            if (minutes < 60) //If data is newer than 30 minutes don't pull from the api
                pullFromApi = false
        }

        if (!pullFromApi) {
            Log.d("REQ", "Pulled from Filesystem")
            val readBytes = cacheFile.readBytes()
            val WeatherDataJson = String(readBytes)
            //  Log.d("ReadFromFile", WeatherDataJson)
            result = Gson().fromJson(WeatherDataJson, WeatherData.WeatherData::class.java)
        } else {
            Log.d("REQ", "Pulled from web")
            val weatherApi =
                RetrofitHelper.getInstance().create(WeatherData.WeatherApi::class.java)
            //Sets latitude and longitude based on provided city
            val lat: Double? = locaton.lat
            val lon: Double? = locaton.lon

            //Reads current language and calls one call api to get weather data
            val languageID: String = getString(R.string.languageId)
            val weatherResult = weatherApi.getWeather(lat, lon, languageID)
            result = weatherResult.body()!!


            val resultByteArray = Gson().toJson(result).toByteArray()
            try {
                File.createTempFile(filename, null, context?.cacheDir)
                cacheFile = File(context?.cacheDir, filename)
                cacheFile.writeBytes(resultByteArray);

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result

    }

    private fun updateUi() {
        try {
            val locationApi =
                RetrofitHelper.getInstance().create(WeatherData.LocationApi::class.java)

            // launching a new coroutine
            GlobalScope.launch {
                //Shows Spinner Thing
                binding.loader.visibility = View.VISIBLE
                //Gets coordinates based on city
                val city = "Leiria"
                val locationResult = locationApi.getCoordinatesForLocation(city)

                //Parses body
                val locationBody = locationResult.body()?.get(0)
                //Log.d("location", locationResult.body().toString())


                //Reads current language and calls one call api to get weather data
                val languageID: String = getString(R.string.languageId)
                val body = locationBody?.let { getWeatherData(it) }
                //Check if it got something
                //Logs Response to logcat
                ///Log.d("WeatherData ", body.toString())
                //Log.d("Current", body?.current?.weather.toString())


                activity?.runOnUiThread {
                    binding.currentCity.text =
                        locationBody?.localNames?.let { getName(it, languageID) }
                    val currentWeatherText =
                        (body?.current?.weather?.get(0)?.description.toString())
                    binding.currentMain.text = currentWeatherText.capitalizeWords()

                    //Display Current temperature
                    (body?.current?.temp?.roundToInt()
                        ?.toString() + "??").also { binding.currentTemperature.text = it }

                    //Put Icon in imageView
                    val icon: String =
                        body?.current?.weather?.get(0)?.icon.toString()
                    val imageURL = "https://openweathermap.org/img/wn/$icon@2x.png"

                    Picasso.with(context)
                        .load(imageURL)
                        .resize(300, 300)
                        .into(binding.weatherIcon)


                    //Puts rest of the week
                    val rvDaily = binding.recyclerView

                    val daily = body?.daily
                    val today = daily?.get(0)
                    binding.currentWeatherCard.setOnClickListener {
                        //Access view by using `it`
                        val intent: Intent = DetailedViewActivity.createIntent(
                            context, //Create intent
                            Gson().toJson(today)
                        ) //Convert clicked data into gson to be passed to new activity
                        startActivity(intent)
                    }

                    //Removes today's weather from the list
                    daily?.removeAt(0)

                    val adapter = DailyAdapter(daily)

                    //Handles item click
                    adapter.onItemClick = { dailyData ->
                        val intent: Intent = DetailedViewActivity.createIntent(
                            context, //Create intent
                            Gson().toJson(dailyData)
                        ) //Convert clicked data into gson to be passed to new activity
                        startActivity(intent)
                    }

                    rvDaily.adapter = adapter
                    rvDaily.layoutManager = LinearLayoutManager(context)

                    // rvDaily.setOnItemClickListener {}
                    //Shows elements after grabbing data
                    binding.currentWeatherCard.visibility = View.VISIBLE
                    hideLoader()

                }


            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
            val myToast = Toast.makeText(this.context, "Error!", Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.BOTTOM, 200, 200)
            myToast.show()
        }

    }

    //Target for picasso
    private fun hideLoader() {
        activity?.runOnUiThread {
            binding.loader.visibility = View.GONE
        }
    }
}