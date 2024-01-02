package com.yic3.lib.dialog

import android.content.Context
import android.os.Bundle
import com.github.gzuliyujiang.wheelpicker.contract.LinkageProvider
import com.yic3.lib.databinding.DialogLocationPickerBinding
import com.yic3.lib.entity.CityEntity
import com.yic3.lib.entity.CountyEntity
import com.yic3.lib.entity.ProvinceEntity
import com.yic3.lib.util.ChinaRegionUtil

class LocationPickerDialog(context: Context, private val onLocation: (province: ProvinceEntity, city: CityEntity, county: CountyEntity) -> Unit): ZZBottomDialog(context) {

    lateinit var binding: DialogLocationPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLocationPickerBinding.inflate(layoutInflater)
        setView(binding.root)
        initView()
    }

    private fun initView() {
        binding.titleTextView.text = "全部区域"
        binding.linkPicker.setData(LocationProvider())
        binding.confirmButton.setOnClickListener {
            val province = binding.linkPicker.firstWheelView.getCurrentItem<ProvinceEntity>()
            val city = binding.linkPicker.secondWheelView.getCurrentItem<CityEntity>()
            val county = binding.linkPicker.thirdWheelView.getCurrentItem<CountyEntity>()

            onLocation.invoke(province, city, county)
            dismiss()
        }
    }

}

class LocationProvider: LinkageProvider {
    override fun firstLevelVisible(): Boolean {
        return true
    }

    override fun thirdLevelVisible(): Boolean {
        return true
    }

    override fun provideFirstData(): MutableList<*> {
        val list = mutableListOf<ProvinceEntity>()
        list.addAll(ChinaRegionUtil.getProvinceList())
        return list
    }

    override fun linkageSecondData(firstIndex: Int): MutableList<*> {
        val list = mutableListOf<CityEntity>()
        val province = provideFirstData()[firstIndex] as ProvinceEntity
        ChinaRegionUtil.getCityByProvince(province.id).let {
            list.addAll(it)
        }
        return list
    }

    override fun linkageThirdData(firstIndex: Int, secondIndex: Int): MutableList<*> {
        val list = mutableListOf<CountyEntity>()
        val city = linkageSecondData(firstIndex)[secondIndex] as CityEntity
        ChinaRegionUtil.getCountyListByCity(city.id)?.let {
            list.addAll(it)
        }
        return list
    }

    override fun findFirstIndex(firstValue: Any?): Int {
        return ChinaRegionUtil.getProvinceList().indexOf(firstValue)
    }

    override fun findSecondIndex(firstIndex: Int, secondValue: Any?): Int {
        return linkageSecondData(firstIndex).indexOf(secondValue)
    }

    override fun findThirdIndex(firstIndex: Int, secondIndex: Int, thirdValue: Any?): Int {
        return linkageThirdData(firstIndex, secondIndex).indexOf(thirdValue)
    }
}