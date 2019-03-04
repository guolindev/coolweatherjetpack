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
import com.coolweather.coolweatherjetpack.data.Resource
import com.coolweather.coolweatherjetpack.ui.MainActivity
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import com.coolweather.coolweatherjetpack.util.InjectorUtil
import kotlinx.android.synthetic.main.activity_weather.*

class ChooseAreaFragment : Fragment() {

    private lateinit var viewModel: ChooseAreaViewModel
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
        viewModel = ViewModelProviders.of(this, InjectorUtil.getChooseAreaModelFactory()).get(ChooseAreaViewModel::class.java)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, viewModel.dataList)
        listView.adapter = adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when {
                viewModel.currentLevel == LEVEL_PROVINCE -> {
                    viewModel.selectedProvince = viewModel.provinceList?.get(position)
                    queryCities()
                }
                viewModel.currentLevel == LEVEL_CITY -> {
                    viewModel.selectedCity = viewModel.cityList?.get(position)
                    queryCounties()
                }
                viewModel.currentLevel == LEVEL_COUNTY -> {
                    val weatherId = viewModel.countyList!![position].weatherId
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
                        weatherActivity.observeWeather(weatherActivity.viewModel.refreshWeather(weatherId, MainActivity.KEY), true)
                    }
                }
            }
        }
        backButton.setOnClickListener {
            if (viewModel.currentLevel == LEVEL_COUNTY) {
                queryCities()
            } else if (viewModel.currentLevel == LEVEL_CITY) {
                queryProvinces()
            }
        }
        if (viewModel.dataList.isEmpty()) {
            queryProvinces()
        }
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private fun queryProvinces() = handleData(viewModel.getProvinceList()) { list ->
        titleText.text = "中国"
        backButton.visibility = View.GONE
        viewModel.dataList.addAll(list.map { it.provinceName })
        viewModel.provinceList = list
        viewModel.currentLevel = LEVEL_PROVINCE
    }

    private fun queryCities() = viewModel.selectedProvince?.let { it ->
        titleText.text = it.provinceName
        backButton.visibility = View.VISIBLE
        handleData(viewModel.getCityList(it.provinceCode)) { list ->
            viewModel.dataList.addAll(list.map { city ->  city.cityName })
            viewModel.cityList = list
            viewModel.currentLevel = LEVEL_CITY
        }
    }

    private fun queryCounties() = viewModel.selectedCity?.let {
        titleText.text = it.cityName
        backButton.visibility = View.VISIBLE
        handleData(viewModel.getCountyList(it.provinceId, it.cityCode)) { list ->
            viewModel.dataList.addAll(list.map { county -> county.countyName })
            viewModel.countyList = list
            viewModel.currentLevel = LEVEL_COUNTY
        }
    }

    private fun <T> handleData(liveData: LiveData<Resource<List<T>>>, action: (List<T>) -> Unit) = liveData.observe(this, Observer { result ->
        if (result?.status == Resource.LOADING) {
            showProgressDialog()
        } else if (result?.data != null && result.status == Resource.SUCCESS) {
            closeProgressDialog()
            viewModel.dataList.clear()
            action(result.data)
            adapter.notifyDataSetChanged()
            listView.setSelection(0)
        } else {
            closeProgressDialog()
            Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show()
        }
    })

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