@file:Suppress("LeakingThis")

package com.example.interpreter.customView.blockView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.example.interpreter.customView.ioView.InputView
import com.example.interpreter.customView.ioView.OutputView
import com.example.interpreter.databinding.BlockViewBinding
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.ioInterfaces.ioTypes.InputFunction
import com.example.interpreter.ioInterfaces.ioTypes.OutputFunction
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.instruction.Instruction

@SuppressLint("ClickableViewAccessibility")
abstract class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), IOContainer {
    val binding = BlockViewBinding.inflate(LayoutInflater.from(context), this)
    
    override val view = this
    
    //todo: remake to hash of <Input, InputView> and <Input, OutputView> to optimal searching
    private val inputViewHash = hashMapOf<Input, InputView>()
    private val outputViewHash = hashMapOf<Output, OutputView>()
    
    override val inputs = mutableListOf<Pair<Input, Output>>()
    override val outputs = mutableListOf<Pair<Output, List<Input>>>()
    
    //todo: error alert in time on type mismatch
    private fun typeMismatch() {
        inputs.forEach {
            if (it.second.name != IO.Name.Fake && it.first.type != it.second.type)
                throw Error("type misMatch: required ${it.first.type} but found ${it.second.type}")
        }
    }
    
    open fun checkError() {
        typeMismatch()
    }
    
    open fun compile(compiler: Compiler): List<Instruction> {
        checkError()
        return listOf()
    }
    
    final override fun addInput(input: Input, to: Input?, before: Boolean) {
        super.addInput(input, to, before)
        val row = InputView(context)
        inputViewHash[input] = row
    
        row.initComponents(input)
        row.setDescription(input.description)
        binding.listOfInputLinearLayout.addView(row, findIndexByInput(input))
    }
    
    final override fun addOutput(output: Output, to: Output?, before: Boolean) {
        super.addOutput(output, to, before)
        val row = OutputView(context)
        outputViewHash[output] = row
        
        row.initComponents(output)
        row.setDescription(output.description)
        binding.listOfOutputLinearLayout.addView(row, findIndexByOutput(output))
    }
    
    override fun removeInput(input: Input) {
        binding.listOfInputLinearLayout.removeViewAt(findIndexByInput(input))
        inputViewHash.remove(input)
        super.removeInput(input)
    }
    
    override fun removeOutput(output: Output) {
        binding.listOfOutputLinearLayout.removeViewAt(findIndexByOutput(output))
        outputViewHash.remove(output)
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
    
    fun findInputByInputRadioButton(view: View): Input? {
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
    
    fun findOutputByOutputRadioButton(view: View): Output? {
        binding.listOfOutputLinearLayout.forEachIndexed { index, it ->
            if (
                ((it as LinearLayout)
                    .getChildAt(0) as LinearLayout)
                    .getChildAt(1) == view
            ) {
                return outputs[index].first
            }
        }
        return null
    }
    
    fun getListOfInputView(): MutableList<View> {
        val list = mutableListOf<View>()
        binding.listOfInputLinearLayout.forEach {
            for (i in binding.listOfInputLinearLayout.children) {
                list.add(((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(0))
            }
        }
        return list
    }
    
    fun getListOfOutputView(): MutableList<View> {
        val list = mutableListOf<View>()
        binding.listOfOutputLinearLayout.forEach {
            for (i in binding.listOfOutputLinearLayout.children) {
                list.add(((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1))
            }
        }
        return list
    }
    
    fun isTwoIOViewInBlock(first: View, second: View): Boolean {
        var flag1 = false
        var flag2 = false
        for (i in getListOfInputView()) {
            if (first == i || second == i) {
                flag1 = true
            }
        }
        for (i in getListOfOutputView()) {
            if (first == i || second == i) {
                flag2 = true
            }
        }
        return (flag1 && flag2)
    }
    
    fun isOutputComplete(output: View) =
        findIndexByOutput(findOutputByOutputRadioButton(output)) == 0 &&
                outputs[0].second.isNotEmpty()
    
    fun isInputComplete(input: View) =
        inputs[findIndexByInput(findInputByInputRadioButton(input))].second.name != IO.Name.Fake
    
    init {
        init()
    }
    
    open fun init(){
        addInput(InputFunction(IO.Name.From, this, "before"))
        addOutput(OutputFunction(IO.Name.To, this, "after"))
    }
}