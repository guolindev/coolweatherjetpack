package com.coolweather.coolweatherjetpack.ui.weather

import androidx.lifecycle.ViewModel
import com.coolweather.coolweatherjetpack.data.WeatherRepository
import com.coolweather.coolweatherjetpack.data.model.weather.Weather

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    var weather: Weather? = null

    var bingPicUrl: String? = null

    fun getWeather(weatherId: String, key: String) = repository.getWeather(weatherId, key)

    fun refreshWeather(weatherId: String, key: String) = repository.refreshWeather(weatherId, key)

    fun isWeatherCached() = repository.isWeatherCached()

    fun getCachedWeather() = repository.getCachedWeather()

    fun getBingPic() = repository.getBingPic()

}