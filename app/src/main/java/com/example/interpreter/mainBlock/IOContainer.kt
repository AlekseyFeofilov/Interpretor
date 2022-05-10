package com.example.interpreter.mainBlock

import android.view.View

interface IOContainer {
    val view: View
    var inputs: MutableList<Pair<Input, Output?>>
    var outputs: MutableList<Pair<Output, List<Input>>>
    
    fun findIndexByInput(element: Input?) =
        inputs.indexOf(inputs.find { it.first == element })
    
    fun findIndexByOutput(element: Output?) =
        outputs.indexOf(outputs.find { it.first == element })
    
    //todo: add before: Int? type
    fun addInput(input: Input, before: Input? = null) {
        val position = findIndexByInput(before)
        
        inputs.add(
            if (position != -1) position else inputs.size,
            Pair(input, null)
        )
    }
    
    fun addInput(inputList: List<Input>, before: Input? = null) {
        inputList.forEachIndexed { index, it ->
            addInput(it, if (index == 0) before else inputList[index - 1])
        }
    }
    
    fun removeInput(input: Input, disconnectInput: Boolean = true) {
        if(disconnectInput) {
            disconnectInput(input)
        }
        
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
        val pair = inputs[index]
        
        if (pair.first.autocomplete) {
            val before = if (index != inputs.size - 1) inputs[index + 1].first else null
            addInput(pair.first.clone(), before)
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
        
        if (input.autocomplete && input.getValue() == null) {
            removeInput(input, false)
            return
        }
        
        inputs[index] = inputs[index].copy(second = null)
    }
    
    fun addOutput(output: Output, before: Output? = null) {
        val index = findIndexByOutput(before)
    
        outputs.add(
            if (index != -1) index else outputs.size,
            Pair(output, listOf())
        )
    }
    
    //todo: make right order (for Input too)
    fun addOutput(outputList: List<Output>, before: Output? = null) {
        outputList.forEachIndexed { index, it ->
            addOutput(it, if (index == 0) before else outputList[index - 1])
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
}