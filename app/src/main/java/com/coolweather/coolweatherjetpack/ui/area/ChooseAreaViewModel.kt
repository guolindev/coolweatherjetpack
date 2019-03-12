package com.coolweather.coolweatherjetpack.ui.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolweather.coolweatherjetpack.data.PlaceRepository
import com.coolweather.coolweatherjetpack.data.model.place.City
import com.coolweather.coolweatherjetpack.data.model.place.County
import com.coolweather.coolweatherjetpack.data.model.place.Province
import kotlinx.coroutines.launch
import java.util.*

class ChooseAreaViewModel(private val repository: PlaceRepository) : ViewModel() {

    var currentLevel = MutableLiveData<Int>()

    var selectedProvince: Province? = null

    var selectedCity: City? = null

    var provinces = MutableLiveData<Result<MutableList<Province>>>()

    var cities = MutableLiveData<Result<MutableList<City>>>()

    var counties = MutableLiveData<Result<MutableList<County>>>()

    var dataList = ArrayList<String>()

    fun getProvinces() = launch({
        provinces.value = Result.success(repository.getProvinceList())
    }, {
        provinces.value = Result.failure(it)
    })

    fun getCities(provinceId: Int) = launch({
        cities.value = Result.success(repository.getCityList(provinceId))
    }, {
        cities.value = Result.failure(it)
    })

    fun getCounties(provinceId: Int, cityId: Int) = launch({
        counties.value = Result.success(repository.getCountyList(provinceId, cityId))
    }, {
        counties.value = Result.failure(it)
    })

    private fun launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) = viewModelScope.launch {
        try {
            block()
        } catch (t: Throwable) {
            error(t)
        }
    }

}