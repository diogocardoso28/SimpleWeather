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
import com.diogo.weatherapp.databinding.FragmentHomeBinding
import com.diogo.weatherapp.ui.home.HomeViewModel
import com.diogo.weatherapp.ui.home.RetrofitHelper
import com.diogo.weatherapp.ui.home.WeatherData
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Math.round


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val Apikey: String = "28c0b3908568301c3399083982a34e36"
    private val weatherData: WeatherData = WeatherData(Apikey)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Updates ui after page loads
        updateUi();

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUi() {
        try {
            val weatherApi = RetrofitHelper.getInstance().create(WeatherData.WeatherApi::class.java)


            //Shows Spinner Thing
            // launching a new coroutine
            GlobalScope.launch {
                binding.loader.visibility = View.VISIBLE;

                val result = weatherApi.getWeather("pt")

                //Check if it got something
                if (result != null) {
                    val body = result.body();
                    //Logs Response to logcat
                    Log.d("WeatherData ", body.toString())
                    Log.d("Current", body?.current?.weather.toString())


                    activity?.runOnUiThread {

                        binding.currentMain.text =
                            (body?.current?.weather?.get(0)?.description.toString() ?: "");
                        //Display Current temperature
                        (body?.current?.temp?.let { it1 ->
                            round(
                                it1
                            ).toString()
                        } + "º").also { binding.currentTemperature.text = it };

                        //Put Icon in imageView
                        val icon: String = body?.current?.weather?.get(0)?.icon.toString() ?: "";
                        val imageURL = "https://openweathermap.org/img/wn/$icon@2x.png"

                        Picasso.with(context)
                            .load(imageURL)
                            .resize(300, 300)
                            .into(binding.weatherIcon);


                        //Shows elements after grabbing data
                        binding.currentWeatherCard.visibility = View.VISIBLE;
                        hideLoader()

                    }

                }

            }
        } catch (e: Exception) {
            Log.e("Error", e.toString());
            val myToast = Toast.makeText(this.context, "Error!", Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.BOTTOM, 200, 200)
            myToast.show()
        }

    }

    //Target for picasso
    private fun hideLoader() {
        activity?.runOnUiThread {
            binding.loader.visibility = View.GONE;
        }
    }
}