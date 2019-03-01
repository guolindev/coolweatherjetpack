package com.coolweather.coolweatherjetpack.ui

import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.util.InjectorUtil
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import android.content.Intent
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (KEY.isEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("请先在MainActivity中配置天气API的Key")
            builder.setCancelable(false)
            builder.setPositiveButton("确定") { _, _ ->
                finish()
            }
            builder.show()
        } else {
            val viewModel = ViewModelProviders.of(this, InjectorUtil.getMainModelFactory()).get(MainViewModel::class.java)
            if (viewModel.isWeatherCached()) {
                val intent = Intent(this, WeatherActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        // 请求天气API的Key，请到http://guolin.tech/api/weather/register申请免费的Key
        const val KEY = "45dd25f63300445e967b461d2037e4f9"
    }

}
