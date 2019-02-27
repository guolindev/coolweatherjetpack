package com.coolweather.coolweatherjetpack.data.model.place

import com.google.gson.annotations.SerializedName
import org.litepal.crud.LitePalSupport

class Province (@SerializedName("name") val provinceName: String, @SerializedName("id") val provinceCode: Int) : LitePalSupport() {
    @Transient val id = 0
}