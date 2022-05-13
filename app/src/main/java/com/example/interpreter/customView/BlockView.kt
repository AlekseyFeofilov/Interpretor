package com.example.interpreter.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.marginEnd
import com.example.interpreter.customView.ioView.InputView
import com.example.interpreter.customView.ioView.OutputView
import com.example.interpreter.databinding.BlockViewBinding
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction

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
    
    override fun removeInput(input: Input) {
        binding.listOfInputLinearLayout.removeViewAt(findIndexByInput(input))
        super.removeInput(input)
    }
    
    override fun removeOutput(output: Output) {
        binding.listOfOutputLinearLayout.removeViewAt(findIndexByOutput(output))
        super.removeOutput(output)
    }
    
    override fun connectInput(input: Input, output: Output) {
        super.connectInput(input, output)
        
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
        
        if (input.isDefault) {
            row.hideDefaultValue()
        }
    }
    
    override fun disconnectInput(input: Input) {
        super.disconnectInput(input)
        
        if (findIndexByInput(input) == -1) return
        
        val row = binding.listOfInputLinearLayout.getChildAt(findIndexByInput(input)) as InputView
        
        if (input.isDefault) {
            row.showDefaultValue()
        }
    }
    
    override fun setHeader(name: String, colorHEX: String) {
        binding.headerTextView.text = name
        binding.headerTextView.setBackgroundColor(Color.parseColor(colorHEX))
    }
    
    private fun findInputByInputRadioButton(view: View): Input? {
        binding.listOfInputLinearLayout.forEachIndexed { index, it ->
            if (
                ((it as LinearLayout)
                    .getChildAt(0) as LinearLayout)
                    .getChildAt(0) == view
            ) {
                return inputs[index].first
            }
        }
        return null
    }
    
    private fun findOutputByOutputRadioButton(view: View): Output? {
        binding.listOfOutputLinearLayout.forEachIndexed { index, it ->
            if (
                ((it as LinearLayout)
                    .getChildAt(0) as LinearLayout)
                    .getChildAt(0) == view
            ) {
                return outputs[index].first
            }
        }
        return null
    }
    
    fun isOutputComplete(output: View) =
        findIndexByOutput(findOutputByOutputRadioButton(output)) == 0 &&
                outputs[0].second.isNotEmpty()
    
    fun isInputComplete(input: View) =
        inputs[findIndexByInput(findInputByInputRadioButton(input))].second != null
    
    init {
        addInput(InputFunction("before", this))
        addOutput(OutputFunction("after", this))
    }
}