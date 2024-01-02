package com.yic3.lib.base

import android.content.Intent
import java.io.Serializable

class SafeIntent: Intent() {

    override fun <T : Serializable?> getSerializableExtra(name: String?, clazz: Class<T>): T? {
        return try {
            super.getSerializableExtra(name, clazz)
        } catch (e: Exception) {
            null
        }
    }

    override fun getSerializableExtra(name: String?): Serializable? {
        return try {
            super.getSerializableExtra(name)
        } catch (e: Exception) {
            null
        }
    }

}