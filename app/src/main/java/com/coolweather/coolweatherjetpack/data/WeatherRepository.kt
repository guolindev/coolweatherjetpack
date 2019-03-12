package com.coolweather.coolweatherjetpack.data

import com.coolweather.coolweatherjetpack.data.db.WeatherDao
import com.coolweather.coolweatherjetpack.data.model.weather.Weather
import com.coolweather.coolweatherjetpack.data.network.CoolWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository private constructor(private val weatherDao: WeatherDao, private val network: CoolWeatherNetwork) {

    suspend fun getWeather(weatherId: String, key: String): Weather {
        var weather = weatherDao.getCachedWeatherInfo()
        if (weather == null) weather = requestWeather(weatherId, key)
        return weather
    }

    suspend fun refreshWeather(weatherId: String, key: String) = requestWeather(weatherId, key)

    suspend fun getBingPic(): String {
        var url = weatherDao.getCachedBingPic()
        if (url == null) url = requestBingPic()
        return url
    }

    suspend fun refreshBingPic() = requestBingPic()

    fun isWeatherCached() = weatherDao.getCachedWeatherInfo() != null

    fun getCachedWeather() = weatherDao.getCachedWeatherInfo()!!

    private suspend fun requestWeather(weatherId: String, key: String) = withContext(Dispatchers.IO) {
        val heWeather = network.fetchWeather(weatherId, key)
        val weather = heWeather.weather!![0]
        weatherDao.cacheWeatherInfo(weather)
        weather
    }

    private suspend fun requestBingPic() =withContext(Dispatchers.IO) {
        val url = network.fetchBingPic()
        weatherDao.cacheBingPic(url)
        url
    }

    companion object {

        private var instance: WeatherRepository? = null

        fun getInstance(weatherDao: WeatherDao, network: CoolWeatherNetwork): WeatherRepository {
            if (instance == null) {
                synchronized(WeatherRepository::class.java) {
                    if (instance == null) {
                        instance = WeatherRepository(weatherDao, network)
                    }
                }
            }
            return instance!!
        }

    }

}