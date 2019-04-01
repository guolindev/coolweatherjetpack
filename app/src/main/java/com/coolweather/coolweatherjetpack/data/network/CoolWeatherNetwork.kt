package com.coolweather.coolweatherjetpack.data.network

import com.coolweather.coolweatherjetpack.data.network.api.PlaceService
import com.coolweather.coolweatherjetpack.data.network.api.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoolWeatherNetwork {

    private val placeService = ServiceCreator.create(PlaceService::class.java)

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun fetchProvinceList() = placeService.getProvinces().await()

    suspend fun fetchCityList(provinceId: Int) = placeService.getCities(provinceId).await()

    suspend fun fetchCountyList(provinceId: Int, cityId: Int) = placeService.getCounties(provinceId, cityId).await()

    suspend fun fetchWeather(weatherId: String) = weatherService.getWeather(weatherId).await()

    suspend fun fetchBingPic() = weatherService.getBingPic().await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }
            })
        }
    }

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