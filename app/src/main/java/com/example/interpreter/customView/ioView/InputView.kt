package com.example.interpreter.customView.ioView

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.widget.doAfterTextChanged
import com.example.interpreter.databinding.InputViewBinding
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean

class InputView constructor(context: Context?) : LinearLayout(context) {
    private val binding = InputViewBinding.inflate(LayoutInflater.from(context), this)
    private var input: Input? = null
    
    private fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
    
    private fun setDefaultVisibility(visible: Boolean) {
        if (input == null) return
        
        if (input!!.isDefault) {
            setVisibility(
                if (input!!.type == IO.Companion.Type.Boolean) binding.inputBooleanSpinner
                else binding.inputDefaultValueEditText,
                visible
            )
        }
    }
    
    fun setDescription(description: String?) {
        if (input == null) return
        
        if (!description.isNullOrEmpty() && !input!!.description.isNullOrEmpty()) {
            binding.inputDescriptionTextView.text = description
        }
    }
    
    fun showDefaultValue() {
        setDefaultVisibility(true)
    }
    
    fun hideDefaultValue() {
        setDefaultVisibility(false)
    }
    
    private fun initInputBooleanSpinner() {
        binding.inputBooleanSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (input as InputBoolean).default =
                    when (parent.getItemAtPosition(position).toString()) {
                        "True" -> true
                        "False" -> false
                        else -> null
                    }
            }
            
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }
    
    private fun initDefaultEditText(type: Int){
        binding.inputDefaultValueEditText.inputType = type
        binding.inputDefaultValueEditText.doAfterTextChanged {
            if (input!!.isEmpty()) input!!.parent.inputAutocomplete(input!!)
            input!!.parseValue(it.toString())
        }
    }
    
    fun initComponents(input: Input) {
        this.input = input
        
        if (input.isDefault) {
            when (input.type) {
                IO.Companion.Type.Boolean -> {
                    removeComponent(2)
                    initInputBooleanSpinner()
                }
                IO.Companion.Type.String -> {
                    removeComponent(3)
                    initDefaultEditText(InputType.TYPE_CLASS_TEXT)
                }
                IO.Companion.Type.Double -> {
                    removeComponent(3)
                    initDefaultEditText(InputType.TYPE_CLASS_NUMBER)
                }
                else -> { }
            }
        } else {
            removeComponent(2, 2)
        }
        
        if (input.description.isNullOrEmpty()) removeComponent(1)
        //if (!inputField) removeComponent(0) todo: init objects
    }
    
    private fun removeComponent(start: Int, count: Int = 1) {
        binding.inputLinearLayout.removeViews(start, count)
    }
    
    ////////////////////////
    
    private var indicator = false
    
    private val connect = OnClickListener {
        it as RadioButton
        if (!indicator) {
            input!!.parent.connectInput(input!!, input!!.parent.outputs[0].first, true)
        } else {
            input!!.parent.disconnectInput(input!!, true)
            it.isChecked = false
        }
        
        indicator = !indicator
    }
    
    init {
        binding.inputRadioButton.setOnClickListener(connect)
    }
}