package com.example.interpreter.customView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.example.interpreter.databinding.InputViewBinding
import com.example.interpreter.mainBlock.IOContainer
import com.example.interpreter.mainBlock.Input
import com.example.interpreter.mainBlock.ioTypes.OutputDouble

class InputView constructor(context: Context?) : LinearLayout(context) {
    private val binding = InputViewBinding.inflate(LayoutInflater.from(context), this)
    private lateinit var input: Input
    
    private var inputField = false
    private var descriptionField = false
    private var defaultValueField = false
    private var isBooleanType = false
    
    private fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
    
    private fun setDefaultVisibility(visible: Boolean) {
        if (defaultValueField) {
            setVisibility(
                if (isBooleanType) binding.inputBooleanSpinner
                else binding.inputDefaultValueEditTextView,
                visible
            )
        }
    }
    
    fun setDescription(description: String?) {
        if (!description.isNullOrEmpty() && descriptionField) {
            binding.inputDescriptionTextView.text = description
        }
    }
    
    fun showDefaultValue() {
        setDefaultVisibility(true)
    }
    
    fun hideDefaultValue() {
        setDefaultVisibility(false)
    }
    
    fun initComponents(
        input: Input,
        inputField: Boolean,
        descriptionInputField: Boolean,
        defaultInputValueField: Boolean,
        isBooleanType: Boolean = false
    ) {
        this.input = input
        
        this.inputField = inputField
        this.descriptionField = descriptionInputField
        this.defaultValueField = defaultInputValueField
        this.isBooleanType = isBooleanType
        
        initRow()
    }
    
    private fun removeComponent(start: Int, count: Int = 1) {
        binding.inputLinearLayout.removeViews(start, count)
    }
    
    private fun initRow() {
        if (defaultValueField) {
            if (isBooleanType) {
                removeComponent(2)
            } else {
                removeComponent(3)
            }
        } else {
            removeComponent(2, 2)
        }
        
        if (!descriptionField) removeComponent(1)
        if (!inputField) removeComponent(0)
    }
    
/*    private var indicator = false
    
    private val connect = OnClickListener {
        it as RadioButton
        if (!indicator) {
            input.parent.connectInput(input, input.parent.outputs[0].first, true)
        } else {
            input.parent.disconnectInput(input, true)
            it.isChecked = false
        }
        
        indicator = !indicator
    }
    
    init {
        binding.inputRadioButton.setOnClickListener(connect)
    }*/
}