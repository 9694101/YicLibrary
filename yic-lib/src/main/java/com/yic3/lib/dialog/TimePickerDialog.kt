package com.yic3.lib.dialog

import android.content.Context
import android.os.Bundle
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity
import com.yic3.lib.databinding.DialogTimePickerBinding
import java.util.Calendar

class TimePickerDialog(context: Context): ZZBottomDialog(context) {

    lateinit var binding: DialogTimePickerBinding
    var timeSelectListener: OnTimeSelectListener? = null

    var defaultHour: Int = 0
    var defaultMinute: Int = 0

    var titleText: String = ""
        set(value) {
            field = value
            if (this::binding.isInitialized) {
                binding.titleTextView.text = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTimePickerBinding.inflate(layoutInflater)
        setView(binding.root)
        initView()
    }

    private fun initView() {
        if (defaultHour == 0 && defaultMinute == 0) {
            val calendar = Calendar.getInstance()
            defaultHour = calendar.get(Calendar.HOUR_OF_DAY)
            defaultMinute = calendar.get(Calendar.MINUTE)
        }

        binding.timePicker.setDefaultValue(TimeEntity.target(defaultHour, defaultMinute, 0))
        binding.timePicker.setResetWhenLinkage(false)

        binding.confirmButton.setOnClickListener {
            timeSelectListener?.onTime(binding.timePicker.selectedHour, binding.timePicker.selectedMinute)
            dismiss()
        }
    }

}

interface OnTimeSelectListener {

    fun onTime(hour: Int, minute: Int)

}