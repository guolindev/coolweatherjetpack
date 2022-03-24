package com.coolweather.coolweatherjetpack.ui.weather

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.databinding.ActivityWeatherBinding
import com.coolweather.coolweatherjetpack.util.InjectorUtil

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(viewModelStore, InjectorUtil.getWeatherModelFactory()).get(WeatherViewModel::class.java) }

    val binding: ActivityWeatherBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_weather) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        binding.viewModel = viewModel
        binding.resId = R.color.colorPrimary
        binding.lifecycleOwner = this
        viewModel.weatherId = if (viewModel.isWeatherCached()) {
            viewModel.getCachedWeather().basic.weatherId
        } else {
            intent.getStringExtra("weather_id")!!
        }
        binding.title.navButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        viewModel.getWeatherFromRepository()
    }

}