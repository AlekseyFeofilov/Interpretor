package com.example.interpreter.ioInterfaces.ioTypes

import com.example.interpreter.ioInterfaces.IO
import com.example.interpreter.ioInterfaces.IOContainer
import com.example.interpreter.ioInterfaces.Input

class InputFunction(
    override val description: String = "",
    override var parent: IOContainer,
    override val autocomplete: Boolean = false
) : Input {
    override val type = IO.Companion.Type.Function
    override val isDefault = false
    override val color = "#8B80F9"
    var default: Unit? = null
    
    override fun parseValue(value: String) {
        //do nothing because isDefault = false
    }
    
    override fun clone(): Input {
        return InputFunction(this.description, this.parent, this.autocomplete)
    }
    
    override fun getValue() = default
}