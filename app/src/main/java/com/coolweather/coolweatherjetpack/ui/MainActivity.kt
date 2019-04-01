package com.coolweather.coolweatherjetpack.ui

import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.util.InjectorUtil
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.coolweather.coolweatherjetpack.ui.area.ChooseAreaFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this, InjectorUtil.getMainModelFactory()).get(MainViewModel::class.java)
        if (viewModel.isWeatherCached()) {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, ChooseAreaFragment()).commit()
        }
    }

}
