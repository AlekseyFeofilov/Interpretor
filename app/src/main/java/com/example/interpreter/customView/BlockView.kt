package com.example.interpreter.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.interpreter.customView.ioView.InputView
import com.example.interpreter.customView.ioView.OutputView
import com.example.interpreter.databinding.BlockViewBinding
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output

@SuppressLint("ClickableViewAccessibility")
open class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), IOContainer {
    val binding = BlockViewBinding.inflate(LayoutInflater.from(context), this)
    
    override val view = this
    override var inputs = mutableListOf<Pair<Input, Output?>>()
    override var outputs = mutableListOf<Pair<Output, List<Input>>>()
    
    override fun addInput(input: Input, to: Input?, before: Boolean) {
        super.addInput(input, to, before)
        val row = InputView(context)
        
        row.initComponents(input)
        row.setDescription(input.description)
        binding.listOfInputLinearLayout.addView(row, findIndexByInput(input))
    }
    
    override fun addOutput(output: Output, to: Output?, before: Boolean) {
        super.addOutput(output, to, before)
        val row = OutputView(context)
    
        row.initComponents(output)
        row.setDescription(output.description)
        binding.listOfOutputLinearLayout.addView(row, findIndexByOutput(output))
    }
    
    override fun removeInput(input: Input, disconnectInput: Boolean) {
        binding.listOfInputLinearLayout.removeViewAt(findIndexByInput(input))
        super.removeInput(input, disconnectInput)
    }
    
    override fun removeOutput(output: Output) {
        binding.listOfOutputLinearLayout.removeViewAt(findIndexByOutput(output))
        super.removeOutput(output)
    }
    
    override fun connectInput(input: Input, output: Output, connectOutput: Boolean) {
        super.connectInput(input, output, connectOutput)
    
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
        
        if(input.isDefault){
            row.hideDefaultValue()
        }
    }
    
    override fun disconnectInput(input: Input, disconnectOutput: Boolean) {
        super.disconnectInput(input, disconnectOutput)
        
        if(findIndexByInput(input) == -1) return
        
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
    
        if(input.isDefault){
            row.showDefaultValue()
        }
    }
    
    override fun setHeader(name: String, colorHEX: String) {
        binding.headerTextView.text = name
        binding.headerTextView.setBackgroundColor(Color.parseColor(colorHEX))
    }
}