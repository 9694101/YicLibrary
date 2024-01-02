package com.yic3.lib.util

object GuideDataUtil {

    const val KEY_PURPOSE = "purpose"

    private val dataMap: MutableMap<String, Any> = mutableMapOf()

    fun put(key: String, value: Any) {
        dataMap[key] = value
    }

    fun putAll(map: Map<String, Any>) {
        dataMap.putAll(map)
    }

    fun get(key: String): Any? {
        return dataMap[key]
    }

    fun clear() {
        dataMap.clear()
    }

    fun getData(): MutableMap<String, Any> {
        return dataMap
    }

}