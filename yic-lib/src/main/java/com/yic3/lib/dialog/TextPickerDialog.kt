package com.yic3.lib.dialog

import android.content.Context
import android.os.Bundle
import com.yic3.lib.databinding.DialogTextPickerBinding

class TextPickerDialog(context: Context): ZZBottomDialog(context) {

    private lateinit var binding: DialogTextPickerBinding
    var textPickListener: OnTextPickListener? = null

    var textList: List<String>? = null
    set(value) {
        field = value
        if (this::binding.isInitialized) {
            binding.textPicker.setData(value)
        }
    }

    var defaultPosition: Int = 0
    set(value) {
        field = value
        if (this::binding.isInitialized) {
            binding.textPicker.setDefaultPosition(value)
        }
    }

    var titleText: String = ""
        set(value) {
            field = value
            if (this::binding.isInitialized) {
                binding.titleTextView.text = value
            }
        }

    private var currentItem: String = ""
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogTextPickerBinding.inflate(layoutInflater)
        setView(binding.root)
        initView()
    }

    private fun initView() {
        binding.titleTextView.text = titleText
        binding.textPicker.setData(textList)
        binding.textPicker.setDefaultPosition(defaultPosition)

        currentItem = textList?.get(defaultPosition) ?: ""
        currentPosition = defaultPosition

        binding.textPicker.setOnOptionSelectedListener { position, item ->
            currentItem = item.toString()
            currentPosition = position
        }

        binding.confirmButton.setOnClickListener {
            if (textPickListener == null) {
                dismiss()
            } else if (textPickListener!!.onText(currentItem, currentPosition)) {
                dismiss()
            }
        }
    }

}

interface OnTextPickListener {

    fun onText(text: String, position: Int): Boolean

}