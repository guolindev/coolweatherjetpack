package com.coolweather.coolweatherjetpack.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coolweather.coolweatherjetpack.data.db.WeatherDao
import com.coolweather.coolweatherjetpack.data.model.weather.HeWeather
import com.coolweather.coolweatherjetpack.data.model.weather.Weather
import com.coolweather.coolweatherjetpack.data.network.CoolWeatherNetwork
import com.coolweather.coolweatherjetpack.util.CoolWeatherExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository private constructor(val weatherDao: WeatherDao, val network: CoolWeatherNetwork) {

    fun getWeather(weatherId: String, key: String): LiveData<Resource<Weather>> {
        val liveData = MutableLiveData<Resource<Weather>>()
        liveData.value = Resource.loading(null)
        CoolWeatherExecutors.diskIO.execute {
            val weather = weatherDao.getCachedWeatherInfo()
            if (weather == null) {
                requestWeather(weatherId, key, liveData)
            } else {
                liveData.postValue(Resource.success(weather))
            }
        }
        return liveData
    }

    fun refreshWeather(weatherId: String, key: String): LiveData<Resource<Weather>> {
        val liveData = MutableLiveData<Resource<Weather>>()
        liveData.value = Resource.loading(null)
        requestWeather(weatherId, key, liveData)
        return liveData
    }

    fun getBingPic(): LiveData<Resource<String>> {
        val liveData = MutableLiveData<Resource<String>>()
        CoolWeatherExecutors.diskIO.execute {
            val bingPic = weatherDao.getCachedBingPic()
            if (bingPic == null) {
                requestBingPic(liveData)
            } else {
                liveData.postValue(Resource.success(bingPic))
            }
        }
        return liveData
    }

    fun isWeatherCached() = weatherDao.getCachedWeatherInfo() != null

    fun getCachedWeather() = weatherDao.getCachedWeatherInfo()!!

    private fun requestWeather(weatherId: String, key: String, liveData: MutableLiveData<Resource<Weather>>) {
        network.fetchWeather(weatherId, key, object : Callback<HeWeather> {
            override fun onFailure(call: Call<HeWeather>, t: Throwable) {
                liveData.postValue(Resource.error("获取天气信息失败", null))
            }

            override fun onResponse(call: Call<HeWeather>, response: Response<HeWeather>) {
                CoolWeatherExecutors.diskIO.execute {
                    val result = response.body()?.weather!![0]
                    weatherDao.cacheWeatherInfo(result)
                    liveData.postValue(Resource.success(result))
                }
            }
        })
    }

    private fun requestBingPic(liveData: MutableLiveData<Resource<String>>) {
        network.fetchBingPic(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                liveData.postValue(Resource.error("获取图片失败", null))
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                CoolWeatherExecutors.diskIO.execute {
                    val result = response.body()
                    weatherDao.cacheBingPic(result)
                    liveData.postValue(Resource.success(result))
                }
            }
        })
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