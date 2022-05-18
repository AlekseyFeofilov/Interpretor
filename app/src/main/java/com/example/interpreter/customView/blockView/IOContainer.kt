package com.example.interpreter.customView.blockView

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.Input
import com.example.interpreter.ioInterfaces.Output
import com.example.interpreter.ioInterfaces.ioTypes.*
import com.example.interpreter.vm.Compiler
import com.example.interpreter.vm.Executor

interface IOContainer {
    val view: View
    
    val inputs: MutableList<Pair<Input, Output>>
    val outputs: MutableList<Pair<Output, List<Input>>>
    
    fun findIndexByInput(element: Input?) =
        inputs.indexOf(inputs.find { it.first == element })
    
    fun findIndexByOutput(element: Output?) =
        outputs.indexOf(outputs.find { it.first == element })
    
    fun addInput(input: Input, to: Input? = null, before: Boolean = true) {
        val position = if (before) findIndexByInput(to) else findIndexByInput(to) + 1
        
        inputs.add(
            if (position != -1) position else inputs.size,
            Pair(input, input.generateCoupleOutput())
        )
    }
    
    fun addInput(inputList: List<Input>, to: Input? = null, before: Boolean = true) {
        inputList.forEachIndexed { index, it ->
            addInput(
                it,
                if (index == 0) to else inputList[index - 1],
                index == 0 && before
            )
        }
    }

/*    //todo: complete for saving code
    fun addInput(stringArray: Array<String>) {
        val input: Input = when (stringArray[1]) {
            IO.Type.Boolean.toString() ->
                InputBoolean(
                    stringArray[2],
                    this,
                    stringArray[3].toBoolean(),
                    stringArray[4].toBoolean()
                )
            
            IO.Type.String.toString() ->
                InputString(
                    stringArray[2],
                    this,
                    stringArray[3].toBoolean(),
                    stringArray[4].toBoolean()
                )
            
            IO.Type.Double.toString() ->
                InputDouble(
                    stringArray[2],
                    this,
                    stringArray[3].toBoolean(),
                    stringArray[4].toBoolean()
                )
            
            else ->
                InputFunction(stringArray[2], this, stringArray[3].toBoolean())
        }
//              "Input, $type, $description, $autocomplete, $isDefault, ${getValue()}"
        input.parseValue(stringArray[5])
    }*/
    
    fun inputAutocomplete(input: Input) {
        if (!input.autocomplete || !input.isEmpty()) return
        addInput(input.clone(), input, false)
    }
    
    fun removeCloneInput(input: Input) {
        if (input.autocomplete && input.isEmpty()) {
            removeInput(input)
        }
    }
    
    fun removeInput(input: Input) {
        disconnectInput(input)
        inputs.remove(inputs[findIndexByInput(input)])
    }
    
    fun removeInput(inputList: List<Input>) {
        inputList.forEach {
            removeInput(it)
        }
    }
    
    fun connectInput(input: Input, output: Output) {
        val index = findIndexByInput(input)
        
        if (index == -1 || inputs[index].second == output) return
        
        inputs[index] = inputs[index].copy(second = output)
        inputAutocomplete(input)
        
        output.parent.connectOutput(output, input)
    }
    
    fun disconnectInput(input: Input) {
        val index = findIndexByInput(input)
        
        if (index == -1 || inputs[index].second.name == IO.Name.Fake) return
        
        val output = inputs[index].second
        inputs[index] = inputs[index].copy(second = inputs[index].first.generateCoupleOutput())
        removeCloneInput(input)
        
        output.parent.disconnectOutput(output, input)
    }
    
    fun addOutput(output: Output, to: Output? = null, before: Boolean = true) {
        val index = if (before) findIndexByOutput(to) else findIndexByOutput(to) + 1
        
        outputs.add(
            if (index != -1) index else outputs.size,
            Pair(output, listOf())
        )
    }
    
    fun addOutput(outputList: List<Output>, to: Output? = null, before: Boolean = true) {
        outputList.forEachIndexed { index, it ->
            addOutput(
                it,
                if (index == 0) to else outputList[index - 1],
                index == 0 && before
            )
        }
    }

/*    fun addOutput(stringArray: Array<String>) {
        val output: Output = when (stringArray[1]) {
            IO.Type.Boolean.toString() ->
                OutputBoolean(stringArray[2], this)
            
            IO.Type.String.toString() ->
                OutputString(stringArray[2], this)
            
            IO.Type.Double.toString() ->
                OutputDouble(stringArray[2], this)
            
            else ->
                OutputFunction(stringArray[2], this)
        }
        
        
    }*/
    
    fun removeOutput(output: Output) {
        disconnectOutputAll(output)
        outputs.remove(outputs[findIndexByOutput(output)])
    }
    
    fun removeOutput(outputList: List<Output>) {
        outputList.forEach {
            removeOutput(it)
        }
    }
    
    fun connectOutput(output: Output, input: Input) {
        val index = findIndexByOutput(output)
        
        if (index == -1 || input in outputs[index].second) return
        
        outputs[index] = outputs[index].copy(second = outputs[index].second + input)
        input.parent.connectInput(input, output)
    }
    
    fun disconnectOutput(output: Output, input: Input) {
        val index = findIndexByOutput(output)
        
        if (index == -1 || input !in outputs[index].second) return
        
        outputs[index] = outputs[index].copy(second = outputs[index].second - input)
        input.parent.disconnectInput(input)
    }
    
    fun disconnectOutputAll(output: Output) {
        val index = findIndexByOutput(output)
        
        val listOfInput = outputs[index].second
        outputs[index] = Pair(output, listOf())
        listOfInput.forEach { it.parent.disconnectInput(it) }
    }
    
    fun setHeader(name: String, colorHEX: String)
    
    /*fun toString(): String{
        var string: String
        var inputsStrings: MutableList<String>
        var outputsStrings: MutableList<String>
        
        inputs.forEach{
            inputsStrings.add(it.first.toString())
            outputsStrings.add(it.second.toString())
        }
        
        return string
    }
    
    fun parse(string: String){
    
    }*/
    
    fun getInputsConnecting(): HashMap<Input, Output> {
        val result = hashMapOf<Input, Output>()
        
        inputs.forEach {
            if (it.second.name != IO.Name.Fake || it.first.getValue() != null && it.first.type != IO.Type.Any) {
                result[it.first] = it.second
            }
        }
        
        return result
    }
    
    fun getOutputsConnecting(): HashMap<Output, List<Input>> {
        val result = hashMapOf<Output, List<Input>>()
        
        outputs.forEach {
            if (it.second.isNotEmpty()) {
                result[it.first] = it.second
            }
        }
        
        return result
    }
    
    fun isInputAvailable(input: Input): Boolean{
        return !((getLinkInput(input).name == IO.Name.Fake && input.getValue() == null) ||
                (input.getValue() != null && input.type == IO.Type.Any))
    }
    
    fun isOutputAvailable(output: Output): Boolean{
        return getLinkOutput(output).isNotEmpty()
    }
    
    
    fun getInputsHash(): HashMap<IO.Name, Any> { /* HashMap<IO.Name, Any = Input | List<Input>> */
        val result = hashMapOf<IO.Name, Any>()
        
        inputs.forEach { pair ->
            when {
                result.containsKey(pair.first.name) || !isInputAvailable(pair.first) -> {
                }
                !pair.first.autocomplete -> {
                    result[pair.first.name] = pair.first
                }
                pair.first.autocomplete -> {
                    result[pair.first.name] =
                        inputs.filter { it.first.name == pair.first.name }.map { it.first }
                }
            }
        }
        
        return result
    }
    
    fun getOutputsHash(): HashMap<IO.Name, Output> {
        return HashMap<IO.Name, Output>().apply {
            outputs.forEach { if (isOutputAvailable(it.first)) this[it.first.name] = it.first }
        }
    }
    
    fun getLinkInput(input: Input): Output {
        return inputs[findIndexByInput(input)].second
    }
    
    fun getLinkOutput(output: Output): List<Input> {
        return outputs[findIndexByOutput(output)].second
    }
    
    fun getInput(name: IO.Name) = inputs.find { it.first.name == name }?.first
    
    fun getInputExecutor(compiler: Compiler, name: IO.Name): Executor {
        compiler.push()
        
        if(isInputAvailable(getInput(name) ?: throw Error("This input isn't exist"))) {
            compiler[name]
        }
        
        return compiler.pop()
    }
    
}