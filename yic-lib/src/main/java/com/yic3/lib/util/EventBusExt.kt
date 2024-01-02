package com.yic3.lib.util

import android.app.Activity
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus

fun Any.postEvent() {
    EventBus.getDefault().post(this)
}

fun Activity.registerEvent() {
    EventBus.getDefault().register(this)
}

fun Fragment.registerEvent() {
    EventBus.getDefault().register(this)
}

fun Activity.unregisterEvent() {
    EventBus.getDefault().unregister(this)
}

fun Fragment.unregisterEvent() {
    EventBus.getDefault().unregister(this)
}