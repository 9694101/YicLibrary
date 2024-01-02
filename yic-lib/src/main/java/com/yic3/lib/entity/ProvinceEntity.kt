package com.yic3.lib.entity

import java.io.Serializable

data class ProvinceEntity(val name: String, val id: String): Serializable {
    override fun toString(): String {
        return name
    }
}

data class CityEntity(val province: String, val name: String, val id: String): Serializable {
    override fun toString(): String {
        return name
    }
}

data class RegionEntity(val name: String, var provinceList: List<ProvinceEntity>): Serializable {
    override fun toString(): String {
        return name
    }
}

data class CountyEntity(val city: String, val name: String, val id: String) {
    override fun toString(): String {
        return name
    }
}