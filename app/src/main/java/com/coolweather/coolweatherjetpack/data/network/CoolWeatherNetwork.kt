package com.coolweather.coolweatherjetpack.data.network

import com.coolweather.coolweatherjetpack.data.model.place.City
import com.coolweather.coolweatherjetpack.data.model.place.County
import com.coolweather.coolweatherjetpack.data.model.place.Province
import com.coolweather.coolweatherjetpack.data.model.weather.HeWeather
import com.coolweather.coolweatherjetpack.data.network.api.PlaceService
import com.coolweather.coolweatherjetpack.data.network.api.WeatherService
import retrofit2.Callback

class CoolWeatherNetwork {

    private val placeService = ServiceCreator.create(PlaceService::class.java)

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    fun fetchProvinceList(callback: Callback<List<Province>>) = placeService.getProvinces().enqueue(callback)

    fun fetchCityList(provinceId: Int, callback: Callback<List<City>>) = placeService.getCities(provinceId).enqueue(callback)

    fun fetchCountyList(provinceId: Int, cityId: Int, callback: Callback<List<County>>) = placeService.getCounties(provinceId, cityId).enqueue(callback)

    fun fetchWeather(weatherId: String, key: String, callback: Callback<HeWeather>) = weatherService.getWeather(weatherId, key).enqueue(callback)

    fun fetchBingPic(callback: Callback<String>) = weatherService.getBingPck().enqueue(callback)

    companion object {

        private var network: CoolWeatherNetwork? = null

        fun getInstance(): CoolWeatherNetwork {
            if (network == null) {
                synchronized(CoolWeatherNetwork::class.java) {
                    if (network == null) {
                        network = CoolWeatherNetwork()
                    }
                }
            }
            return network!!
        }

    }

}