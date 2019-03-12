package com.coolweather.coolweatherjetpack.ui.area

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.ui.MainActivity
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import com.coolweather.coolweatherjetpack.util.InjectorUtil
import kotlinx.android.synthetic.main.activity_weather.*

class ChooseAreaFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this, InjectorUtil.getChooseAreaModelFactory()).get(ChooseAreaViewModel::class.java) }
    private var progressDialog: ProgressDialog? = null
    private lateinit var titleText: TextView
    private lateinit var backButton: Button
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.choose_area, container, false)
        titleText = view.findViewById(R.id.title_text)
        backButton = view.findViewById(R.id.back_button)
        listView = view.findViewById(R.id.list_view)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, viewModel.dataList)
        listView.adapter = adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when {
                viewModel.currentLevel.value == LEVEL_COUNTY -> {
                    val countyList = viewModel.counties.value
                    if (countyList != null) {
                        val weatherId = countyList.getOrThrow()[position].weatherId
                        if (activity is MainActivity) {
                            val intent = Intent(activity, WeatherActivity::class.java)
                            intent.putExtra("weather_id", weatherId)
                            startActivity(intent)
                            activity?.finish()
                        } else if (activity is WeatherActivity) {
                            val weatherActivity = activity as WeatherActivity
                            weatherActivity.drawerLayout.closeDrawers()
                            weatherActivity.swipeRefresh.isRefreshing = true
                            weatherActivity.mWeatherId = weatherId
                            weatherActivity.viewModel.refreshWeather(weatherId, MainActivity.KEY)
                        }
                    }
                }
                viewModel.currentLevel.value == LEVEL_CITY -> {
                    val cityList = viewModel.cities.value
                    if (cityList != null) {
                        viewModel.selectedCity = cityList.getOrThrow()[position]
                        queryCounties()
                    }
                }
                viewModel.currentLevel.value == LEVEL_PROVINCE -> {
                    val provinceList = viewModel.provinces.value
                    if (provinceList != null) {
                        viewModel.selectedProvince = provinceList.getOrThrow()[position]
                        queryCities()
                    }
                }
            }
        }
        backButton.setOnClickListener {
            if (viewModel.currentLevel.value == LEVEL_COUNTY) {
                queryCities()
            } else if (viewModel.currentLevel.value == LEVEL_CITY) {
                queryProvinces()
            }
        }
        observe()
    }

    private fun observe() {
        observe(viewModel.provinces) { list -> viewModel.dataList.addAll(list.map { it.provinceName }) }
        observe(viewModel.cities) { list -> viewModel.dataList.addAll(list.map { city ->  city.cityName }) }
        observe(viewModel.counties) { list -> viewModel.dataList.addAll(list.map { county -> county.countyName }) }
        viewModel.currentLevel.observe(this, Observer { level ->
            when (level) {
                LEVEL_PROVINCE -> {
                    titleText.text = "中国"
                    backButton.visibility = View.GONE
                }
                LEVEL_CITY -> {
                    titleText.text = viewModel.selectedProvince?.provinceName
                    backButton.visibility = View.VISIBLE
                }
                LEVEL_COUNTY -> {
                    titleText.text = viewModel.selectedCity?.cityName
                    backButton.visibility = View.VISIBLE
                }
            }
        })
        if (viewModel.dataList.isEmpty()) {
            queryProvinces()
        }
    }

    private fun <T> observe(liveData: LiveData<Result<MutableList<T>>>, block: (List<T>) -> Unit) {
        liveData.observe(this, Observer { result ->
            if (result.isSuccess) {
                viewModel.dataList.clear()
                block(result.getOrThrow())
                adapter.notifyDataSetChanged()
                listView.setSelection(0)
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            }
            closeProgressDialog()
        })
    }

    private fun queryProvinces() {
        showProgressDialog()
        viewModel.currentLevel.value = LEVEL_PROVINCE
        viewModel.getProvinces()
    }

    private fun queryCities() = viewModel.selectedProvince?.let {
        showProgressDialog()
        viewModel.currentLevel.value = LEVEL_CITY
        viewModel.getCities(it.provinceCode)
    }

    private fun queryCounties() = viewModel.selectedCity?.let {
        showProgressDialog()
        viewModel.currentLevel.value = LEVEL_COUNTY
        viewModel.getCounties(it.provinceId, it.cityCode)
    }

    /**
     * 显示进度对话框
     */
    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog?.setMessage("正在加载...")
            progressDialog?.setCanceledOnTouchOutside(false)
        }
        progressDialog?.show()
    }

    /**
     * 关闭进度对话框
     */
    private fun closeProgressDialog() {
        progressDialog?.dismiss()
    }

    companion object {
        const val LEVEL_PROVINCE = 0
        const val LEVEL_CITY = 1
        const val LEVEL_COUNTY = 2
    }

}