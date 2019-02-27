package com.coolweather.coolweatherjetpack.util

import com.coolweather.coolweatherjetpack.data.PlaceRepository
import com.coolweather.coolweatherjetpack.data.WeatherRepository
import com.coolweather.coolweatherjetpack.data.db.CoolWeatherDatabase
import com.coolweather.coolweatherjetpack.data.network.CoolWeatherNetwork
import com.coolweather.coolweatherjetpack.ui.MainModelFactory
import com.coolweather.coolweatherjetpack.ui.area.ChooseAreaModelFactory
import com.coolweather.coolweatherjetpack.ui.weather.WeatherModelFactory

object InjectorUtil {

    private fun getPlaceRepository() = PlaceRepository.getInstance(CoolWeatherDatabase.getPlaceDao(), CoolWeatherNetwork.getInstance())

    private fun getWeatherRepository() = WeatherRepository.getInstance(CoolWeatherDatabase.getWeatherDao(), CoolWeatherNetwork.getInstance())

    fun getChooseAreaModelFactory() = ChooseAreaModelFactory(getPlaceRepository())

    fun getWeatherModelFactory() = WeatherModelFactory(getWeatherRepository())

    fun getMainModelFactory() = MainModelFactory(getWeatherRepository())

}