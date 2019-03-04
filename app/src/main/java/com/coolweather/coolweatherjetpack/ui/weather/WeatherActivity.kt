package com.coolweather.coolweatherjetpack.ui.weather

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.data.Resource
import com.coolweather.coolweatherjetpack.data.model.weather.Weather
import com.coolweather.coolweatherjetpack.ui.MainActivity
import com.coolweather.coolweatherjetpack.util.InjectorUtil
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*

class WeatherActivity : AppCompatActivity() {

    lateinit var viewModel: WeatherViewModel

    lateinit var mWeatherId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)
        viewModel = ViewModelProviders.of(this, InjectorUtil.getWeatherModelFactory()).get(WeatherViewModel::class.java)
        if (viewModel.isWeatherCached()) {
            mWeatherId = viewModel.getCachedWeather().basic.weatherId
            showWeatherInfo(viewModel.getCachedWeather())
        } else {
            mWeatherId = intent.getStringExtra("weather_id")
            weatherLayout.visibility = View.INVISIBLE
            observeWeather(viewModel.getWeather(mWeatherId, MainActivity.KEY), false)
        }
        observeBindPic(viewModel.getBingPic())
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            observeWeather(viewModel.refreshWeather(mWeatherId, MainActivity.KEY), true)
        }
        navButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun observeWeather(liveData: LiveData<Resource<Weather>>, refresh: Boolean) {
        if (viewModel.weather == null || refresh) {
            liveData.observe(this, Observer { result ->
                if (result?.status == Resource.LOADING) {
                } else if (result?.data != null && result.status == Resource.SUCCESS) {
                    showWeatherInfo(result.data)
                    swipeRefresh.isRefreshing = false
                    viewModel.weather = result.data
                } else {
                    Toast.makeText(this, result?.message, Toast.LENGTH_SHORT).show()
                    swipeRefresh.isRefreshing = false
                }
            })
        } else {
            showWeatherInfo(viewModel.weather!!)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun observeBindPic(liveData: LiveData<Resource<String>>) {
        if (viewModel.bingPicUrl == null) {
            liveData.observe(this, Observer { result ->
                if (result?.data != null && result.status == Resource.SUCCESS) {
                    Glide.with(this).load(result.data).into(bingPicImg)
                    viewModel.bingPicUrl = result.data
                } else {
                    Toast.makeText(this, result?.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Glide.with(this).load(viewModel.bingPicUrl).into(bingPicImg)
        }
    }

    private fun showWeatherInfo(weather: Weather) {
        val cityName = weather.basic.cityName
        val updateTime = weather.basic.update.updateTime.split(" ")[1]
        val degree = weather.now.temperature + "℃"
        val weatherInfo = weather.now.more.info
        titleCity.text = cityName
        titleUpdateTime.text = updateTime
        degreeText.text = degree
        weatherInfoText.text = weatherInfo
        forecastLayout.removeAllViews()
        for (forecast in weather.forecastList) {
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateText = view.findViewById(R.id.dateText) as TextView
            val infoText = view.findViewById(R.id.infoText) as TextView
            val maxText = view.findViewById(R.id.maxText) as TextView
            val minText = view.findViewById(R.id.minText) as TextView
            dateText.text = forecast.date
            infoText.text = forecast.more.info
            maxText.text = forecast.temperature.max
            minText.text = forecast.temperature.min
            forecastLayout.addView(view)
        }
        aqiText.text = weather.aqi.city.aqi
        pm25Text.text = weather.aqi.city.pm25
        val comfort = "舒适度：" + weather.suggestion.comfort.info
        val carWash = "洗车指数：" + weather.suggestion.carWash.info
        val sport = "运动建议：" + weather.suggestion.sport.info
        comfortText.text = comfort
        carWashText.text = carWash
        sportText.text = sport
        weatherLayout.visibility = View.VISIBLE
    }

}