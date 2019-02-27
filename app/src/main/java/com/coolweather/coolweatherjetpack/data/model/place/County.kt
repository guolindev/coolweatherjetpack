package com.coolweather.coolweatherjetpack.data.model.place

import com.google.gson.annotations.SerializedName
import org.litepal.crud.LitePalSupport

class County (@SerializedName("name") val countyName: String, @SerializedName("weather_id") val weatherId: String) : LitePalSupport() {
    @Transient val id = 0
    var cityId = 0
}