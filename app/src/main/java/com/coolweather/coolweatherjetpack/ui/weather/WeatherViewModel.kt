package com.coolweather.coolweatherjetpack.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolweather.coolweatherjetpack.data.WeatherRepository
import com.coolweather.coolweatherjetpack.data.model.weather.Weather
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    var weather = MutableLiveData<Result<Weather>>()

    var bingPicUrl = MutableLiveData<Result<String>>()

    fun getWeather(weatherId: String, key: String) {
        launch ({
            weather.value = Result.success(repository.getWeather(weatherId, key))
        }, {
            weather.value = Result.failure(it)
        })
        getBingPic(false)
    }

    fun refreshWeather(weatherId: String, key: String) {
        launch ({
            weather.value = Result.success(repository.refreshWeather(weatherId, key))
        }, {
            weather.value = Result.failure(it)
        })
        getBingPic(true)
    }

    fun isWeatherCached() = repository.isWeatherCached()

    fun getCachedWeather() = repository.getCachedWeather()

    private fun getBingPic(refresh: Boolean) {
        launch({
            val url = if (refresh) repository.refreshBingPic() else repository.getBingPic()
            bingPicUrl.value = Result.success(url)
        }, {
            bingPicUrl.value = Result.failure(it)
        })
    }

    private fun launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) = viewModelScope.launch {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
        }
    }

}