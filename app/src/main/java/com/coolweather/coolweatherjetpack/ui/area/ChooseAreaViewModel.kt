package com.coolweather.coolweatherjetpack.ui.area

import androidx.lifecycle.ViewModel
import com.coolweather.coolweatherjetpack.data.PlaceRepository
import com.coolweather.coolweatherjetpack.data.model.place.City
import com.coolweather.coolweatherjetpack.data.model.place.County
import com.coolweather.coolweatherjetpack.data.model.place.Province

class ChooseAreaViewModel(private val repository: PlaceRepository) : ViewModel() {

    var currentLevel: Int = 0

    var selectedProvince: Province? = null

    var selectedCity: City? = null

    var provinceList: List<Province>? = null

    var cityList: List<City>? = null

    var countyList: List<County>? = null

    var dataList = ArrayList<String>()

    fun getProvinceList() = repository.getProvinceList()

    fun getCityList(provinceId: Int) = repository.getCityList(provinceId)

    fun getCountyList(provinceId: Int, cityId: Int) = repository.getCountyList(provinceId, cityId)

}