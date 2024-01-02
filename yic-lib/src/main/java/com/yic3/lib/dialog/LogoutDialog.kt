package com.yic3.lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.yic3.lib.databinding.DialogLogoutBinding

class LogoutDialog(context: Context, val listener: View.OnClickListener): Dialog(context, com.yic3.lib.R.style.Translucent_HALF) {

    lateinit var binding: DialogLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.let {
            it.attributes = it.attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }
        binding.confirmButton.setOnClickListener {
            listener.onClick(it)
            dismiss()
        }
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
}