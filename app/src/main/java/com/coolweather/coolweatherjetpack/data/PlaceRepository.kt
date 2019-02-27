package com.coolweather.coolweatherjetpack.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coolweather.coolweatherjetpack.data.db.PlaceDao
import com.coolweather.coolweatherjetpack.data.model.place.City
import com.coolweather.coolweatherjetpack.data.model.place.County
import com.coolweather.coolweatherjetpack.data.model.place.Province
import com.coolweather.coolweatherjetpack.data.network.CoolWeatherNetwork
import com.coolweather.coolweatherjetpack.util.CoolWeatherExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceRepository private constructor(val placeDao: PlaceDao, private val network: CoolWeatherNetwork) {

    fun getProvinceList(): LiveData<Resource<List<Province>>> {
        val liveData = MutableLiveData<Resource<List<Province>>>()
        liveData.value = Resource.loading(null)
        CoolWeatherExecutors.diskIO.execute {
            val list = placeDao.getProvinceList()
            if (list.isEmpty()) {
                network.fetchProvinceList(object : Callback<List<Province>> {
                    override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                        t.printStackTrace()
                        liveData.postValue(Resource.error("加载失败", null))
                    }

                    override fun onResponse(call: Call<List<Province>>, response: Response<List<Province>>) {
                        CoolWeatherExecutors.diskIO.execute {
                            val result = response.body()
                            placeDao.saveProvinceList(result)
                            liveData.postValue(Resource.success(result))
                        }
                    }
                })
            } else {
                liveData.postValue(Resource.success(list))
            }
        }
        return liveData
    }

    fun getCityList(provinceId: Int): LiveData<Resource<List<City>>> {
        val liveData = MutableLiveData<Resource<List<City>>>()
        liveData.value = Resource.loading(null)
        CoolWeatherExecutors.diskIO.execute {
            val list = placeDao.getCityList(provinceId)
            if (list.isEmpty()) {
                network.fetchCityList(provinceId, object : Callback<List<City>> {
                    override fun onFailure(call: Call<List<City>>, t: Throwable) {
                        t.printStackTrace()
                        liveData.postValue(Resource.error("加载失败", null))
                    }

                    override fun onResponse(call: Call<List<City>>, response: Response<List<City>>) {
                        CoolWeatherExecutors.diskIO.execute {
                            val result = response.body()
                            result?.forEach { it.provinceId = provinceId }
                            placeDao.saveCityList(result)
                            liveData.postValue(Resource.success(result))
                        }
                    }
                })
            } else {
                liveData.postValue(Resource.success(list))
            }
        }
        return liveData
    }

    fun getCountyList(provinceId: Int, cityId: Int): LiveData<Resource<List<County>>> {
        val liveData = MutableLiveData<Resource<List<County>>>()
        liveData.value = Resource.loading(null)
        CoolWeatherExecutors.diskIO.execute {
            val list = placeDao.getCountyList(cityId)
            if (list.isEmpty()) {
                network.fetchCountyList(provinceId, cityId, object : Callback<List<County>> {
                    override fun onFailure(call: Call<List<County>>, t: Throwable) {
                        t.printStackTrace()
                        liveData.postValue(Resource.error("加载失败", null))
                    }

                    override fun onResponse(call: Call<List<County>>, response: Response<List<County>>) {
                        CoolWeatherExecutors.diskIO.execute {
                            val result = response.body()
                            result?.forEach { it.cityId = cityId }
                            placeDao.saveCountyList(result)
                            liveData.postValue(Resource.success(result))
                        }
                    }
                })
            } else {
                liveData.postValue(Resource.success(list))
            }
        }
        return liveData
    }

    companion object {

        private var instance: PlaceRepository? = null

        fun getInstance(placeDao: PlaceDao, network: CoolWeatherNetwork): PlaceRepository {
            if (instance == null) {
                synchronized(PlaceRepository::class.java) {
                    if (instance == null) {
                        instance = PlaceRepository(placeDao, network)
                    }
                }
            }
            return instance!!
        }

    }

}