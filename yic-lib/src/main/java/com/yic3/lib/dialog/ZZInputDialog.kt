package com.yic3.lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.blankj.utilcode.util.KeyboardUtils
import com.yic3.lib.R
import com.yic3.lib.databinding.DialogEditContentBinding

class ZZInputDialog : Dialog, View.OnClickListener {

    private lateinit var binding: DialogEditContentBinding
    private var config: InputConfig? = null
    var confirmListener: OnInputConfirmListener? = null

    constructor(context: Context) : super(context, R.style.Translucent_HALF)

    constructor(context: Context, theme: Int) : super(context, theme)

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.message_positive -> {
                if (config?.isMustInput == true) {
                    if (binding.contentEditText.text.isNullOrEmpty()) {
                        return
                    }
                }
                confirmListener?.onInputContent(binding.contentEditText.text.toString())
            }
        }
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogEditContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnDismissListener {
            // 需要在activity中开启ImmersionBar.keyboardEnable(true)
            KeyboardUtils.hideSoftInput(binding.contentEditText)
        }
        setView()
    }

    private fun setView() {
        if (!::binding.isInitialized || config == null) {
            return
        }
        binding.apply {
            messageTitle.text = config!!.title
            contentEditText.hint = config!!.hint
            contentEditText.setText(config!!.content)
            messagePositive.text = config!!.positive
            messagePositive.setOnClickListener(this@ZZInputDialog)
            messageNegative.setOnClickListener(this@ZZInputDialog)

            contentEditText.postDelayed({
                // BaseActivity中点击非EditText会关闭输入法，所以这里需要加延迟
                KeyboardUtils.showSoftInput(contentEditText)
            }, 300)
        }
    }

    fun show(config: InputConfig) {
        this.config = config
        setView()
        show()
    }

}

class InputConfig {

    var title: String = ""
    var hint: String = "请输入内容"
    var content: String = ""
    var positive: String = "确定"
    var isMustInput: Boolean = true

}

interface OnInputConfirmListener {
    fun onInputContent(content: String)
}