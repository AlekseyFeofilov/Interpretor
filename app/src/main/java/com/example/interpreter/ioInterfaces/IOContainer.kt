package com.example.interpreter.ioInterfaces

import android.view.View
import com.example.interpreter.ioInterfaces.ioTypes.InputBoolean

interface IOContainer {
    val view: View
    var inputs: MutableList<Pair<Input, Output?>>
    var outputs: MutableList<Pair<Output, List<Input>>>
    
    fun findIndexByInput(element: Input?) =
        inputs.indexOf(inputs.find { it.first == element })
    
    fun findIndexByOutput(element: Output?) =
        outputs.indexOf(outputs.find { it.first == element })
    
    fun addInput(input: Input, to: Input? = null, before: Boolean = true) {
        val position = if (before) findIndexByInput(to) else findIndexByInput(to) + 1
        
        inputs.add(
            if (position != -1) position else inputs.size,
            Pair(input, null)
        )
    }
    
    fun addInput(stringArray: Array<String>){
        /*val input = when(stringArray[1]){
            IO.Companion.Type.Boolean.toString() -> InputBoolean(stringArray[2], this, stringArray[3].toBoolean())
        }*/
        
        //input.parse()
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
    
    fun inputAutocomplete(input: Input){
        if(!input.autocomplete || !input.isEmpty()) return
        addInput(input.clone(), input, false)
    }
    
    fun removeCloneInput(input: Input){
        if (input.autocomplete && input.isEmpty()){
            removeInput(input, false)
        }
    }
    
    fun removeInput(input: Input, disconnectInput: Boolean = true) {
        if(disconnectInput) { disconnectInput(input) }
        
        inputs.remove(inputs[findIndexByInput(input)])
    }
    
    fun removeInput(inputList: List<Input>) {
        inputList.forEach {
            disconnectInput(it)
            removeInput(it)
        }
    }
    
    fun connectInput(input: Input, output: Output, connectOutput: Boolean = false) {
        val index = findIndexByInput(input)
        
        if (index == -1 || inputs[index].second == output) return
        
        inputs[index] = inputs[index].copy(second = output)
        
        if (input.autocomplete) {
            inputAutocomplete(input)
        }
        
        if (connectOutput) {
            output.parent.connectOutput(output, input)
        }
    }
    
    fun disconnectInput(input: Input, disconnectOutput: Boolean = false) {
        val index = findIndexByInput(input)
        
        if (index == -1 || inputs[index].second == null) return
        
        val pair = inputs[index]
        
        if(disconnectOutput) {
            pair.second!!
                .parent
                .disconnectOutput(pair.second!!, input)
        }
        
        if (input.autocomplete && input.isEmpty()) {
            removeCloneInput(input)
            return
        }
        
        inputs[index] = inputs[index].copy(second = null)
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
                index == 0 && before)
        }
    }
    
    fun removeOutput(output: Output) {
        disconnectOutputAll(output)
        outputs.remove(outputs[findIndexByOutput(output)])
    }
    
    fun removeOutput(outputList: List<Output>) {
        outputList.forEach {
            disconnectOutputAll(it)
            removeOutput(it)
        }
    }
    
    fun connectOutput(output: Output, input: Input, connectInput: Boolean = false) {
        val index = findIndexByOutput(output)
        
        if (index == -1 || input in outputs[index].second) return
        
        outputs[index] = outputs[index].copy(second = outputs[index].second + input)
        
        if(connectInput) {
            input.parent.connectInput(input, output)
        }
    }
    
    fun disconnectOutput(output: Output, input: Input, disconnectInput: Boolean = false) {
        val index = findIndexByOutput(output)
    
        if (index == -1 || input !in outputs[index].second) return
    
        outputs[index] = outputs[index].copy(second = outputs[index].second - input)
        
        if(disconnectInput) {
            input.parent.disconnectInput(input)
        }
    }
    
    fun disconnectOutputAll(output: Output) {
        val index = findIndexByOutput(output)
        
        outputs[index].second.forEach { it.parent.disconnectInput(it, true) }
        
        outputs[index] = Pair(output, listOf())
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
}