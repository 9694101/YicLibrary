package com.yic3.lib.dialog

import android.content.Context
import android.os.Bundle
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.yic3.lib.databinding.DialogDatePickerBinding

class DatePickerDialog(context: Context, private val startDateEntity: DateEntity, private val endDateEntity: DateEntity): ZZBottomDialog(context) {

    var datePickListener: OnDatePickListener? = null
    lateinit var binding: DialogDatePickerBinding

    var titleText: String = ""
    set(value) {
        field = value
        if (this::binding.isInitialized) {
            binding.titleTextView.text = value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDatePickerBinding.inflate(layoutInflater)
        setView(binding.root)
        initView()
    }

    private fun initView() {
        binding.titleTextView.text = titleText

        binding.datePicker.setRange(startDateEntity, endDateEntity)
        binding.datePicker.setResetWhenLinkage(false)

        binding.confirmButton.setOnClickListener {
            binding.datePicker.let {
                datePickListener?.onDate(it.selectedYear, it.selectedMonth, it.selectedDay)
            }
            dismiss()
        }
    }

}

interface OnDatePickListener {

    fun onDate(year: Int, month: Int, day: Int)

}