package com.diogo.weatherapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diogo.weatherapp.R
import com.diogo.weatherapp.databinding.FragmentHomeBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        return if (languageId == "pt") {
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
    } }

    private fun updateUi() {
        try {
            val weatherApi = RetrofitHelper.getInstance().create(WeatherData.WeatherApi::class.java)
            val locationApi =
                RetrofitHelper.getInstance().create(WeatherData.LocationApi::class.java)

            // launching a new coroutine
            GlobalScope.launch {
                //Shows Spinner Thing

                binding.loader.visibility = View.VISIBLE
                //Gets coordinates based on city
                val city = "Lisboa"
                val locationResult = locationApi.getCoordinatesForLocation(city)
                //Parses body
                val locationBody = locationResult.body()?.get(0)
                Log.d("location", locationResult.body().toString())

                //Sets latitude and longitude based on provided city
                val lat: Double? = locationBody?.lat
                val lon: Double? = locationBody?.lon

                //Reads current language and calls one call api to get weather data
                val languageID: String = getString(R.string.languageId)
                val result = weatherApi.getWeather(lat, lon, languageID)

                //Check if it got something
                val body = result.body()
                //Logs Response to logcat
                Log.d("WeatherData ", body.toString())
                Log.d("Current", body?.current?.weather.toString())


                activity?.runOnUiThread {
                    binding.currentCity.text =
                        locationBody?.localNames?.let { getName(it, languageID) }
                    val currentWeatherText =
                        (body?.current?.weather?.get(0)?.description.toString())
                    binding.currentMain.text = currentWeatherText.capitalizeWords()

                    //Display Current temperature
                    (body?.current?.temp?.roundToInt()?.toString() + "º").also { binding.currentTemperature.text = it }

                    //Put Icon in imageView
                    val icon: String =
                        body?.current?.weather?.get(0)?.icon.toString()
                    val imageURL = "https://openweathermap.org/img/wn/$icon@2x.png"

                    Picasso.with(context)
                        .load(imageURL)
                        .resize(300, 300)
                        .into(binding.weatherIcon)


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