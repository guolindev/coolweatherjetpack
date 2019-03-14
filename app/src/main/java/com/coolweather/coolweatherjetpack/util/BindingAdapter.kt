package com.coolweather.coolweatherjetpack.util

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.data.model.weather.Weather

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
        val dateText = view.findViewById(R.id.dateText) as TextView
        val infoText = view.findViewById(R.id.infoText) as TextView
        val maxText = view.findViewById(R.id.maxText) as TextView
        val minText = view.findViewById(R.id.minText) as TextView
        dateText.text = forecast.date
        infoText.text = forecast.more.info
        maxText.text = forecast.temperature.max
        minText.text = forecast.temperature.min
        addView(view)
    }
}