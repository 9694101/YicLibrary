package com.yic3.lib.util

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.google.gson.reflect.TypeToken
import com.yic3.lib.entity.CountyEntity
import com.yic3.lib.entity.CityEntity
import com.yic3.lib.entity.ProvinceEntity
import com.yic3.lib.entity.RegionEntity

object ChinaRegionUtil {

    private var _provinceList = mutableListOf<ProvinceEntity>()
    private val _cityMap = mutableMapOf<String, List<CityEntity>>()
    private val _countyMap = mutableMapOf<String, List<CountyEntity>>()

    fun getShowName(): String? {
        UserInfoManager.userInfo?.let {
            return getShowName(it.provinceId ?: "", it.cityId ?: "", it.countyId ?: "")
        }
        return null
    }

    fun getShowName(provinceId: String, cityId: String, countyId: String): String? {
        if (countyId.isNotEmpty()) {
            getCountyById(countyId)?.let {
                if (it.name != "市辖区") {
                    return it.name
                }
            }
        }
        if (cityId.isNotEmpty()) {
            getCityById(cityId)?.let {
                if (it.name != "市辖区") {
                    return it.name
                }
            }
        }
        if (provinceId.isNotEmpty()) {
            getProvinceById(provinceId)?.let {
                return it.name
            }
        }
        return "全国"
    }

    fun getReginList(): List<RegionEntity> {
        val regionJson = ResourceUtils.readAssets2String("region.json")
        val regionType = object : TypeToken<List<RegionEntity>>(){}.type
        return GsonUtils.fromJson(regionJson, regionType)
    }

    fun getProvinceList(): List<ProvinceEntity> {
        if (_provinceList.isEmpty()) {
            val provinceJson = ResourceUtils.readAssets2String("province.json")
            val provinceType = object : TypeToken<List<ProvinceEntity>>(){}.type
            _provinceList.addAll(GsonUtils.fromJson(provinceJson, provinceType))
        }
        return _provinceList
    }

    fun getCityByProvince(provinceId: String): List<CityEntity> {
        initCity()
        val list = mutableListOf<CityEntity>()
        _cityMap[provinceId]?.let {
            list.addAll(it)
        }
        if (list.size > 1 || provinceId == "0") {
            list.add(0, CityEntity("", "全部", ""))
        }
        return list
    }

    private fun initCity() {
        if (_cityMap.isEmpty()) {
            val cityJson = ResourceUtils.readAssets2String("city.json")
            val cityType = object : TypeToken<Map<String, List<CityEntity>>>(){}.type
            _cityMap.putAll(GsonUtils.fromJson(cityJson, cityType))
        }
    }

    fun getCityById(cityId: String?): CityEntity? {
        cityId ?: return null
        initCity()
        var cityEntity: CityEntity? = null
        _cityMap.forEach { (_, u) ->
            u.find { it.id == cityId }?.let {
                cityEntity = it
                return@forEach
            }
        }
        return cityEntity
    }

    fun getProvinceById(provinceId: String?): ProvinceEntity? {
        if (provinceId.isNullOrEmpty()) {
            return null
        }
        return getProvinceList().find {
            it.id == provinceId
        }
    }

    fun getCountyListByCity(cityId: String?): List<CountyEntity>? {
        initCounty()
        val list = mutableListOf<CountyEntity>()
        _countyMap[cityId]?.let {
            list.addAll(it)
        }
        list.add(0, CountyEntity(cityId ?: "0", "全部", ""))
        return list
    }

    private fun initCounty() {
        if (_countyMap.isEmpty()) {
            val cityJson = ResourceUtils.readAssets2String("county.json")
            val cityType = object : TypeToken<Map<String, Any>>(){}.type
            val map: Map<String, Any> = GsonUtils.fromJson(cityJson, cityType)
            map.forEach { (t, u) ->
                if (u is List<*>) {
                    _countyMap[t] = u.map {
                        val data = it as Map<*, *>
                        CountyEntity(data["city"].toString(), data["name"].toString(), data["id"].toString())
                    }
                }
            }
        }
    }

    fun getCountyById(countyId: String?): CountyEntity? {
        if (countyId.isNullOrEmpty()) {
            return null
        }
        initCounty()
        _countyMap.forEach { entry ->
            entry.value.find {
                it.id.contains(countyId)
            }?.let {
                return it
            }
        }
        return null
    }

    fun getCityByCounty(countyId: String): CityEntity? {
        initCounty()
        var cityId: String? = null
        _countyMap.forEach { (t, u) ->
            u.find {
                it.id.contains(countyId)
            }?.let {
                cityId = t
                return@forEach
            }
        }
        return cityId?.let {
            getCityById(it)
        }
    }

    fun getProvinceByCity(cityId: String): ProvinceEntity? {
        initCity()
        var provinceId: String? = null
        _cityMap.forEach { (t, u) ->
            u.find {
                it.id.contains(cityId)
            }?.let {
                provinceId = t
                return@forEach
            }
        }
        return provinceId?.let {
            getProvinceById(it)
        }
    }

}