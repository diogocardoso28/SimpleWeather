package com.diogo.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.diogo.weatherapp.ui.home.HomeFragment;
import com.diogo.weatherapp.ui.home.WeatherData;

public class DetailedViewActivity extends AppCompatActivity {
    private static String WEATHER_DATA = "Hello World!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        //Log.d(HomeFragment.)
        Toast.makeText(this,"HEYYY",Toast.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context, int weatherData){

        return new Intent(context,DetailedViewActivity.class).putExtra(WEATHER_DATA,weatherData);
    }
}