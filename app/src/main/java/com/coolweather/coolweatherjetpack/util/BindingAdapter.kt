package com.coolweather.coolweatherjetpack.util

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.data.model.weather.Weather
import com.coolweather.coolweatherjetpack.databinding.ForecastItemBinding

@BindingAdapter("bind:loadBingPic")
fun ImageView.loadBingPic(url: String?) {
    if (url != null) Glide.with(context).load(url).into(this)
}

@BindingAdapter("bind:colorSchemeResources")
fun SwipeRefreshLayout.colorSchemeResources(resId: Int) {
    setColorSchemeResources(resId)
}

@BindingAdapter("bind:showForecast")
fun LinearLayout.showForecast(weather: Weather?) = weather?.let {
    removeAllViews()
    for (forecast in it.forecastList) {
        val view = LayoutInflater.from(context).inflate(R.layout.forecast_item, this, false)
        DataBindingUtil.bind<ForecastItemBinding>(view)?.forecast = forecast
        addView(view)
    }
}