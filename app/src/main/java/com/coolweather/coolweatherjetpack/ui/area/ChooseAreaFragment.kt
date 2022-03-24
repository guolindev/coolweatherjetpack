package com.coolweather.coolweatherjetpack.ui.area

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.coolweather.coolweatherjetpack.R
import com.coolweather.coolweatherjetpack.databinding.ChooseAreaBinding
import com.coolweather.coolweatherjetpack.ui.MainActivity
import com.coolweather.coolweatherjetpack.ui.weather.WeatherActivity
import com.coolweather.coolweatherjetpack.util.InjectorUtil

class ChooseAreaFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this, InjectorUtil.getChooseAreaModelFactory()).get(ChooseAreaViewModel::class.java) }
    private var progressDialog: ProgressDialog? = null
    private var binding: ChooseAreaBinding? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.choose_area, container, false)
        binding = DataBindingUtil.bind(view)
        binding?.viewModel = viewModel
        adapter = ChooseAreaAdapter(requireContext(), R.layout.simple_item, viewModel.dataList)
        binding?.listView?.adapter = adapter
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                observe()

                owner.lifecycle.removeObserver(this)
            }
        })
    }

    private fun observe() {
        viewModel.currentLevel.observe(this) { level ->
            when (level) {
                LEVEL_PROVINCE -> {
                    binding?.titleText?.text = "中国"
                    binding?.backButton?.visibility = View.GONE
                }
                LEVEL_CITY -> {
                    binding?.titleText?.text = viewModel.selectedProvince?.provinceName
                    binding?.backButton?.visibility = View.VISIBLE
                }
                LEVEL_COUNTY -> {
                    binding?.titleText?.text = viewModel.selectedCity?.cityName
                    binding?.backButton?.visibility = View.VISIBLE
                }
            }
        }
        viewModel.dataChanged.observe(this) {
            adapter.notifyDataSetChanged()
            binding?.listView?.setSelection(0)
            closeProgressDialog()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showProgressDialog()
            else closeProgressDialog()
        }
        viewModel.areaSelected.observe(this) { selected ->
            if (selected && viewModel.selectedCounty != null) {
                if (activity is MainActivity) {
                    val intent = Intent(activity, WeatherActivity::class.java)
                    intent.putExtra("weather_id", viewModel.selectedCounty!!.weatherId)
                    startActivity(intent)
                    activity?.finish()
                } else if (activity is WeatherActivity) {
                    val weatherActivity = activity as WeatherActivity
                    weatherActivity.binding.drawerLayout.closeDrawers()
                    weatherActivity.viewModel.weatherId = viewModel.selectedCounty!!.weatherId
                    weatherActivity.viewModel.refreshWeather()
                }
                viewModel.areaSelected.value = false
            }
        }
        if (viewModel.dataList.isEmpty()) {
            viewModel.getProvinces()
        }
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