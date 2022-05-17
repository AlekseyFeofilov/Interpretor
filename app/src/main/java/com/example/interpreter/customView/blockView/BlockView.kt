package com.example.interpreter.customView.blockView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
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
import io.ktor.util.*
import java.util.concurrent.Executor

@SuppressLint("ClickableViewAccessibility")
abstract class BlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), IOContainer {
    val binding = BlockViewBinding.inflate(LayoutInflater.from(context), this)
    
    override val view = this
    //todo: remake to hash of <Input, InputView> and <Input, OutputView> to optimal searching
    override var inputs = mutableListOf<Pair<Input, Output?>>()
    override var outputs = mutableListOf<Pair<Output, List<Input>>>()
    
    //todo: error alert in time on type mismatch
    
    private fun typeMismatch() {
        inputs.forEach {
            if (it.second != null && it.first.type != it.second!!.type)
                throw Error("type misMatch: required ${it.first.type} but found ${it.second!!.type}")
        }
    }
    
    open fun checkError() {
        typeMismatch()
    }
    
    open fun compile(compiler: Compiler): List<Instruction> {
        checkError()
        return listOf()
    }
    
    //todo: add fake output by default
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
    
    fun getInputs(): HashMap<Input, Output> {
        val result = hashMapOf<Input, Output>()
        
        inputs.forEach {
            when {
                it.second != null -> result[it.first] = it.second!!
                it.first.getValue() != null -> TODO("add fake output")
            }
        }
        
        return result
    }
    
    fun getOutputs(): HashMap<Output, List<Input>> {
        val result = hashMapOf<Output, List<Input>>()
        
        outputs.forEach {
            if (it.second.isNotEmpty()) {
                result[it.first] = it.second
            }
        }
        
        return result
    }

    fun getInputsHash(): HashMap<IO.Name, Any> {
        val result = hashMapOf<IO.Name, Any>()
        
        inputs.forEach { pair ->
            when {
                result.containsKey(pair.first.name) -> {  }
                !(pair.second != null || pair.first.getValue() != null) -> {  }
                !pair.first.autocomplete -> {
                    result[pair.first.name] = pair.first
                }
                pair.first.autocomplete -> {
                    result[pair.first.name] = inputs.filter { it.first.name ==  pair.first.name }.map { it.first }
                }
            }
        }
        
        return result
    }
    
/*    fun getInputsHash(): HashMap<IO.Name, List<Input>> {
        val result = hashMapOf<IO.Name, List<Input>>()
        
        inputs.forEach { pair ->
            if (!result.containsKey(pair.first.name) &&
                (pair.second != null || pair.first.getValue() != null)
            ) {
                result[pair.first.name] =
                    inputs.filter { it.first.name == pair.first.name }.map { it.first }
            }
        }
        
        return result
    }*/
    
    fun getOutputsHash(): HashMap<IO.Name, Output> {
        return HashMap<IO.Name, Output>().apply {
            outputs.forEach { if (it.second.isNotEmpty()) this[it.first.name] = it.first }
        }
    }
    
    fun getLinkInput(input: Input): Output? {
        return inputs[findIndexByInput(input)].second
    }
    
    fun getLinkOutput(output: Output): List<Input> {
        return outputs[findIndexByOutput(output)].second
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
        binding.listOfInputLinearLayout.forEachIndexed { index, it ->
            for (i in binding.listOfInputLinearLayout.children) {
                list.add(((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(0))
            }
        }
        return list
    }
    
    fun getListOfOutputView(): MutableList<View> {
        val list = mutableListOf<View>()
        binding.listOfOutputLinearLayout.forEachIndexed { index, it ->
            for (i in binding.listOfOutputLinearLayout.children) {
                list.add(((i as LinearLayout).getChildAt(0) as LinearLayout).getChildAt(1))
            }
        }
        return list
    }
    
    fun isOutputComplete(output: View) =
        findIndexByOutput(findOutputByOutputRadioButton(output)) == 0 &&
                outputs[0].second.isNotEmpty()
    
    fun isInputComplete(input: View) =
        inputs[findIndexByInput(findInputByInputRadioButton(input))].second != null
    
    
    init {
        addInput(InputFunction(IO.Name.From, this, "before"))
        addOutput(OutputFunction(IO.Name.To, this, "after"))
    }
}